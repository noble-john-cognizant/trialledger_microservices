package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.SampleStorageResponseDTO;
import com.cts.trialledger.entity.SampleStorage;
import org.springframework.stereotype.Component;

@Component
public class SampleStorageMapper {

    public SampleStorageResponseDTO toResponseDTO(SampleStorage s) {
        return SampleStorageResponseDTO.builder()
                .storageId(s.getStorageId())
                .sampleId(s.getSample().getSampleId())
                .freezerId(s.getFreezerId())
                .shelf(s.getShelf())
                .box(s.getBox())
                .position(s.getPosition())
                .storedAt(s.getStoredAt())
                .retrievedAt(s.getRetrievedAt())
                .build();
    }
}