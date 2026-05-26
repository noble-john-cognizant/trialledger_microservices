package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.dto.AssayRunRequestDTO;
import com.cts.trialledger.dto.AssayRunResponseDTO;
import com.cts.trialledger.dto.ProvenanceRequestDTO;
import com.cts.trialledger.entity.AssayRun;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.exception.AssayRunNotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.AssayRunMapper;
import com.cts.trialledger.repository.AssayRunRepository;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssayRunServiceImpl implements AssayRunService {

    private final AssayRunRepository assayRunRepository;
    private final SampleRepository sampleRepository;
    private final ProvenanceClient provenanceClient;
    private final AssayRunMapper assayRunMapper;

    @Value("${assay.result.storage-path:./results}")
    private String storagePath;

    @Override
    public List<AssayRunResponseDTO> getAllAssayRuns() {

        List<AssayRunResponseDTO> collect = assayRunRepository.findAll()
                .stream()
                .map(assayRunMapper::toResponseDTO)
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public AssayRunResponseDTO getAssayRunById(Long assayId) {

        AssayRun assayRun = assayRunRepository.findById(assayId)
                .orElseThrow(() -> new AssayRunNotFoundException(assayId));


        return assayRunMapper.toResponseDTO(assayRun);
    }

    @Override
    public AssayRunResponseDTO createAssayRun(AssayRunRequestDTO requestDTO) {

        Sample sample = sampleRepository.findById(requestDTO.getSampleId())
                .orElseThrow(() -> new SampleNotFoundException(requestDTO.getSampleId()));

        AssayRun assayRun = AssayRun.builder()
                .sample(sample)
                .instrumentId(requestDTO.getInstrumentId())
                .operatorId(requestDTO.getOperatorId())
                .runDate(LocalDateTime.now())
                .protocolRef(requestDTO.getProtocolRef())
                .resultUri(null)
                .metadataJson(requestDTO.getMetadataJson())
                .build();

        AssayRun saved = assayRunRepository.save(assayRun);

        try {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Map<String, Object> resultData = Map.of(
                    "assayId",      saved.getAssayId(),
                    "sampleId",     sample.getSampleId(),
                    "studyId",      sample.getStudyId(),
                    "instrumentId", requestDTO.getInstrumentId(),
                    "operatorId",   requestDTO.getOperatorId(),
                    "protocolRef",  requestDTO.getProtocolRef(),
                    "runDate",      LocalDateTime.now().toString(),
                    "metadata",     requestDTO.getMetadataJson() != null
                            ? requestDTO.getMetadataJson() : "{}"
            );

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = "assay-" + saved.getAssayId()
                    + "-sample-" + sample.getSampleId()
                    + "-" + timestamp + ".json";

            Path filePath = dir.resolve(fileName);
            Files.writeString(filePath, new ObjectMapper().writeValueAsString(resultData));

            saved.setResultUri(filePath.toString());
            saved = assayRunRepository.save(saved);

            log.info("[AssayRunService] Result file saved | assayId={} path={}",
                    saved.getAssayId(), filePath);

        } catch (Exception e) {
            log.error("[AssayRunService] Failed to save result file | assayId={} error={}",
                    saved.getAssayId(), e.getMessage());
//            saved.setResultUri(requestDTO.getResultUri());
            saved.setResultUri("./results/" + saved.getAssayId() + ".json");
            saved = assayRunRepository.save(saved);

        }

        try {
            Map<String, Object> map = Map.of(
                    "sampleId",   sample.getSampleId(),
                    "operatorId", assayRun.getOperatorId()
            );
            ProvenanceRequestDTO request = new ProvenanceRequestDTO(
                    "CREATE_ASSAY",
                    "assay_run",
                    UserUtil.getCurrentUserId(),
                    saved.getAssayId(),
                    new ObjectMapper().writeValueAsString(map)
            );
            provenanceClient.recordProvenanceData(request);
        } catch (Exception e) {
            log.warn("[AssayRunService] Provenance recording failed: {}", e.getMessage());
        }

        return assayRunMapper.toResponseDTO(saved);
    }

    @Override
    public List<AssayRunResponseDTO> getAssaysBySample(Long sampleId) {

        sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        List<AssayRunResponseDTO> collect = assayRunRepository
                .findBySample_SampleId(sampleId)
                .stream()
                .map(assayRunMapper::toResponseDTO)
                .collect(Collectors.toList());


        return collect;
    }

    @Override
    public List<AssayRunResponseDTO> getAssaysByOperator(Long operatorId) {

        List<AssayRunResponseDTO> assays = assayRunRepository
                .findByOperatorId(operatorId)
                .stream()
                .map(assayRunMapper::toResponseDTO)
                .toList();

        if (assays.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No assay runs found for operator ID: " + operatorId
            );
        }


        return assays;
    }

    @Override
    public List<AssayRunResponseDTO> getAssaysByInstrument(Long instrumentId) {

        List<AssayRunResponseDTO> assays = assayRunRepository
                .findByInstrumentId(instrumentId)
                .stream()
                .map(assayRunMapper::toResponseDTO)
                .toList();

        if (assays.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No assay runs found for instrument ID: " + instrumentId
            );
        }
        return assays;
    }

    @Override
    public Resource downloadResult(Long assayId) {
        AssayRun assayRun = assayRunRepository.findById(assayId)
                .orElseThrow(() -> new AssayRunNotFoundException(assayId));

        String uri = assayRun.getResultUri();
        if (uri == null || uri.isBlank()) {
            throw new ResourceNotFoundException("No result file recorded for assay id=" + assayId);
        }

        Path filePath = Paths.get(uri);
        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException(
                    "Result file not found on server for assay id=" + assayId + " (path=" + uri + ")");
        }

        return new FileSystemResource(filePath);
    }
}