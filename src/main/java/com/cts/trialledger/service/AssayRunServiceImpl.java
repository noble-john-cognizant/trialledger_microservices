package com.cts.trialledger.service.impl;

import com.cts.trialledger.dto.AssayRunRequestDTO;
import com.cts.trialledger.dto.AssayRunResponseDTO;
import com.cts.trialledger.entity.AssayRun;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.exception.AssayRunNotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.AssayRunMapper;
import com.cts.trialledger.repository.AssayRunRepository;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.service.AssayRunService;
//import com.cts.trialledger.service.AuditService;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssayRunServiceImpl implements AssayRunService {

    private final AssayRunRepository assayRunRepository;
    private final SampleRepository sampleRepository;
//    private final AuditService auditService;
//    private final ProvenanceRecordUtil provenanceRecordUtil;
    private final AssayRunMapper assayRunMapper;

    @Override
    public List<AssayRunResponseDTO> getAllAssayRuns() {

        List<AssayRunResponseDTO> collect = assayRunRepository.findAll()
                .stream()
                .map(assayRunMapper::toResponseDTO)
                .collect(Collectors.toList());

//        auditService.storeAudit(
//                "VIEW_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId() + " viewed all assays"
//        );

        return collect;
    }

    @Override
    public AssayRunResponseDTO getAssayRunById(Long assayId) {

        AssayRun assayRun = assayRunRepository.findById(assayId)
                .orElseThrow(() -> new AssayRunNotFoundException(assayId));

//        auditService.storeAudit(
//                "VIEW_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed assay with id: " + assayRun.getAssayId()
//        );

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
                .runDate(requestDTO.getRunDate())
                .protocolRef(requestDTO.getProtocolRef())
                .resultUri(requestDTO.getResultUri())
                .metadataJson(requestDTO.getMetadataJson())
                .build();

        AssayRun saved = assayRunRepository.save(assayRun);

//        auditService.storeAudit(
//                "CREATE_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " created assay with id: " + saved.getAssayId()
//        );

        Map<String, Object> map = Map.of(
                "sampleId", sample.getSampleId(),
                "operatorId", assayRun.getOperatorId()
        );

//        provenanceRecordUtil.saveProvenanceRecord(
//                "CREATE_ASSAY",
//                "assay_run",
//                saved.getAssayId(),
//                map
//        );

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

//        auditService.storeAudit(
//                "VIEW_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed assay by sample id: " + sampleId
//        );

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

//        auditService.storeAudit(
//                "VIEW_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed assay by operator id: " + operatorId
//        );

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

//        auditService.storeAudit(
//                "VIEW_ASSAY",
//                "assay_run",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed assay by instrument id: " + instrumentId
//        );

        return assays;
    }
}