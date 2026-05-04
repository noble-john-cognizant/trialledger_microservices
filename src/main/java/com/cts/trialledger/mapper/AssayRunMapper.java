package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.AssayRunResponseDTO;
import com.cts.trialledger.entity.AssayRun;
import org.springframework.stereotype.Component;

@Component
public class AssayRunMapper {

    public AssayRunResponseDTO toResponseDTO(AssayRun assayRun) {
        return AssayRunResponseDTO.builder()
                .assayId(assayRun.getAssayId())
                .sampleId(assayRun.getSample().getSampleId())
                .instrumentId(assayRun.getInstrumentId())
                .operatorId(assayRun.getOperatorId())
                .runDate(assayRun.getRunDate())
                .protocolRef(assayRun.getProtocolRef())
                .resultUri(assayRun.getResultUri())
                .metadataJson(assayRun.getMetadataJson())
                .build();
    }
}