package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.SampleRequestDTO;
import com.cts.trialledger.dto.SampleResponseDTO;
import com.cts.trialledger.entity.Sample;
import org.springframework.stereotype.Component;

@Component
public class SampleMapper {

    public Sample toEntity(SampleRequestDTO requestDTO) {
        return Sample.builder()
                .participantId(requestDTO.getParticipantId())
                .studyId(requestDTO.getStudyId())
                .sampleType(requestDTO.getSampleType())
                .collectedAt(requestDTO.getCollectedAt())
                .collectedBy(requestDTO.getCollectedBy())
                .initialLocation(requestDTO.getInitialLocation())
                .status(requestDTO.getStatus())
                .build();
    }

    public SampleResponseDTO toResponseDTO(Sample sample) {
        return SampleResponseDTO.builder()
                .sampleId(sample.getSampleId())
                .participantId(sample.getParticipantId())
                .studyId(sample.getStudyId())
                .sampleType(sample.getSampleType())
                .collectedAt(sample.getCollectedAt())
                .collectedBy(sample.getCollectedBy())
                .initialLocation(sample.getInitialLocation())
                .status(sample.getStatus())
                .build();
    }
}