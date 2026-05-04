package com.cts.trialledger.service;

import com.cts.trialledger.dto.SampleStorageRequestDTO;
import com.cts.trialledger.dto.SampleStorageResponseDTO;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.entity.SampleStorage;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.SampleStorageMapper;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.repository.SampleStorageRepository;
//import com.cts.trialledger.service.SampleStorageService;
//import com.cts.trialledger.service.AuditService;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SampleStorageServiceImpl implements SampleStorageService {

    private final SampleStorageRepository sampleStorageRepository;
    private final SampleRepository sampleRepository;
//    private final AuditService auditService;
//    private final ProvenanceRecordUtil provenanceRecordUtil;
    private final SampleStorageMapper sampleStorageMapper;

    @Override
    public SampleStorageResponseDTO storeSample(Long sampleId, SampleStorageRequestDTO dto) {

        Sample sample = sampleRepository.findById(sampleId).orElseThrow(() -> new SampleNotFoundException(sampleId));

        SampleStorage storage = SampleStorage.builder()
                .sample(sample)
                .freezerId(dto.getFreezerId())
                .shelf(dto.getShelf())
                .box(dto.getBox())
                .position(dto.getPosition())
                .storedAt(LocalDateTime.now())
                .build();

        SampleStorage saved = sampleStorageRepository.save(storage);

//        auditService.storeAudit(
//                "STORE_SAMPLE",
//                "sample_storage",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " stored sample of sample id: " + sampleId
//        );

        Map<String, Object> map = Map.of(
                "sampleId", sampleId,
                "freezerId", saved.getFreezerId(),
                "shelf", saved.getShelf(),
                "box", saved.getBox()
        );

//        provenanceRecordUtil.saveProvenanceRecord(
//                "STORE_SAMPLE",
//                "sample_storage",
//                saved.getStorageId(),
//                map
//        );

        return sampleStorageMapper.toResponseDTO(saved);
    }

    @Override
    public SampleStorageResponseDTO retrieveSample(Long storageId) {

        SampleStorage storage = sampleStorageRepository.findById(storageId)
                .orElseThrow(() -> new ResourceNotFoundException("Storage entry not found"));

        storage.setRetrievedAt(LocalDateTime.now());
        SampleStorage saved = sampleStorageRepository.save(storage);

//        auditService.storeAudit(
//                "RETRIEVE_SAMPLE",
//                "sample_storage",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " retrieve sample from storage id: " + storageId
//        );

        return sampleStorageMapper.toResponseDTO(saved);
    }

    @Override
    public List<SampleStorageResponseDTO> getStorageHistory(Long sampleId) {

        sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        List<SampleStorageResponseDTO> list = sampleStorageRepository
                .findBySample_SampleId(sampleId)
                .stream()
                .map(sampleStorageMapper::toResponseDTO)
                .toList();

//        auditService.storeAudit(
//                "VIEW_STORAGE_HISTORY",
//                "sample_storage",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " retrieve storage history of sample id: " + sampleId
//        );

        return list;
    }
}