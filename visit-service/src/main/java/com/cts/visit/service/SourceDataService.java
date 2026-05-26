package com.cts.visit.service;

import com.cts.visit.client.ProvenanceClient;
import com.cts.visit.dto.ProvenanceRequestDTO;
import com.cts.visit.dto.SourceDataRequestDto;
import com.cts.visit.dto.SourceDataResponseDto;
import com.cts.visit.entity.SourceData;
import com.cts.visit.entity.Visit;
import com.cts.visit.exception.ResourceNotFoundException;
import com.cts.visit.mapper.SourceDataMapper;
import com.cts.visit.repository.SourceDataRepository;
import com.cts.visit.repository.VisitRepository;
import com.cts.visit.util.FileHashUtil;
import com.cts.visit.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SourceDataService {

    private final SourceDataRepository sourceDataRepository;
    private final VisitRepository visitRepository;
    private final ProvenanceClient provenanceClient;

    public SourceDataService(SourceDataRepository sourceDataRepository,
                             VisitRepository visitRepository,
                             ProvenanceClient provenanceClient) {
        this.sourceDataRepository = sourceDataRepository;
        this.visitRepository = visitRepository;
        this.provenanceClient = provenanceClient;
    }

    // ── Result record for file streaming ─────────────────────────────────────
    // Carries the Resource, detected content-type, and filename back to the controller
    public record FileResourceResult(Resource resource, String contentType, String filename) {}

    // 1. Capture source data
    public SourceDataResponseDto addSourceData(SourceDataRequestDto request) throws JsonProcessingException {

        // Validate Visit (same service)
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));

        String filePath = request.getDataUri();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException("Source file not found at path: " + filePath);
        }

        SourceData sourceData = new SourceData();
        sourceData.setVisit(visit);
        sourceData.setCollectedBy(request.getCollectedBy());
        sourceData.setDataType(request.getDataType());
        sourceData.setDataUri(request.getDataUri());
        sourceData.setCollectedAt(request.getCollectedAt());

        String hash = FileHashUtil.generateFileSHA256Hash(filePath);
        sourceData.setHash(hash);

        SourceData savedData = sourceDataRepository.save(sourceData);

        Map<String, Object> map = Map.of(
                "collectedBy", savedData.getCollectedBy(),
                "dataType", savedData.getDataType(),
                "visitId", visit.getVisitId(),
                "participantId", visit.getParticipantId());
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO(
                "ADD_SOURCE_DATA", "source_data", UserUtil.getCurrentUserId(),
                sourceData.getSourceId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(dto);

        return SourceDataMapper.toResponseDto(savedData);
    }

    // 2. Get source data by ID
    public SourceDataResponseDto getSourceDataById(Long sourceId) {
        SourceData sourceData = sourceDataRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source data not found"));
        return SourceDataMapper.toResponseDto(sourceData);
    }

    // 3. Verify source data integrity
    public boolean verifySourceDataHash(Long sourceId) {
        SourceData sourceData = sourceDataRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source data not found"));

        String filePath = sourceData.getDataUri();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException("Source file not found at path: " + filePath);
        }

        String currentHash = FileHashUtil.generateFileSHA256Hash(filePath);
        return currentHash.equals(sourceData.getHash());
    }

    // 4. Get all source data for a visit
    public List<SourceDataResponseDto> getSourceDataByVisitId(Long visitId) {
        List<SourceData> sourceDataList = sourceDataRepository.findByVisit_VisitId(visitId);
        if (sourceDataList.isEmpty()) {
            throw new ResourceNotFoundException("No source data found for visit id: " + visitId);
        }
        return sourceDataList.stream()
                .map(SourceDataMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // ── NEW METHOD ────────────────────────────────────────────────────────────
    // 5. Stream the actual file so the browser can display it
    public FileResourceResult getSourceFile(Long sourceId) {
        SourceData sourceData = sourceDataRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source data not found"));

        Path path = Paths.get(sourceData.getDataUri());
        File file = path.toFile();

        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException(
                    "Source file not found at path: " + sourceData.getDataUri());
        }

        // Detect MIME type from the file extension / content
        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (Exception e) {
            contentType = null;
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream"; // fallback — browser will prompt download
        }

        return new FileResourceResult(
                new FileSystemResource(file),
                contentType,
                file.getName()
        );
    }
}
