package com.cts.visit.service;

import com.cts.visit.dto.SourceDataRequestDto;
import com.cts.visit.dto.SourceDataResponseDto;
import com.cts.visit.entity.SourceData;
import com.cts.visit.entity.Visit;
import com.cts.visit.exception.ResourceNotFoundException;
import com.cts.visit.mapper.SourceDataMapper;
import com.cts.visit.repository.SourceDataRepository;
import com.cts.visit.repository.VisitRepository;
import com.cts.visit.util.FileHashUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SourceDataService {

    private final SourceDataRepository sourceDataRepository;
    private final VisitRepository visitRepository;

    public SourceDataService(SourceDataRepository sourceDataRepository,
                             VisitRepository visitRepository) {
        this.sourceDataRepository = sourceDataRepository;
        this.visitRepository = visitRepository;
    }

    // 1. Capture source data
    public SourceDataResponseDto addSourceData(SourceDataRequestDto request) {

        // Validate Visit (same service)
        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        // Note: User validation should be done via a UserClient (Feign) if a user-service exists.
        // Skipped for now since user-service is not in scope.

        // Validate file existence
        String filePath = request.getDataUri();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException(
                    "Source file not found at path: " + filePath);
        }

        SourceData sourceData = new SourceData();
        sourceData.setVisit(visit);
        sourceData.setCollectedBy(request.getCollectedBy());
        sourceData.setDataType(request.getDataType());
        sourceData.setDataUri(request.getDataUri());
        sourceData.setCollectedAt(request.getCollectedAt());

        // Generate SHA-256 hash of FILE CONTENT
        String hash = FileHashUtil.generateFileSHA256Hash(filePath);
        sourceData.setHash(hash);

        SourceData savedData = sourceDataRepository.save(sourceData);
        return SourceDataMapper.toResponseDto(savedData);
    }

    // 2. Get source data by ID
    public SourceDataResponseDto getSourceDataById(Long sourceId) {

        SourceData sourceData = sourceDataRepository.findById(sourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Source data not found"));

        return SourceDataMapper.toResponseDto(sourceData);
    }

    // 3. Verify source data integrity (hash verification)
    public boolean verifySourceDataHash(Long sourceId) {

        SourceData sourceData = sourceDataRepository.findById(sourceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Source data not found"));

        String filePath = sourceData.getDataUri();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException(
                    "Source file not found at path: " + filePath);
        }

        String currentHash = FileHashUtil.generateFileSHA256Hash(filePath);
        return currentHash.equals(sourceData.getHash());
    }

    // 4. Get all source data for a Visit
    public List<SourceDataResponseDto> getSourceDataByVisitId(Long visitId) {

        List<SourceData> sourceDataList =
                sourceDataRepository.findByVisit_VisitId(visitId);

        if (sourceDataList.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No source data found for visit id: " + visitId);
        }

        return sourceDataList.stream()
                .map(SourceDataMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
