package com.cts.trialledger.service;

import com.cts.trialledger.dto.ApiResponseDTO;
import com.cts.trialledger.dto.SampleRequestDTO;
import com.cts.trialledger.dto.SampleResponseDTO;
import com.cts.trialledger.model.SampleStatus;

import java.util.List;

public interface SampleService {

    List<SampleResponseDTO> getAllSamples();

    SampleResponseDTO getSampleById(Long sampleId);

    SampleResponseDTO createSample(SampleRequestDTO requestDTO);

    List<SampleResponseDTO> getSamplesByParticipant(Long participantId);

    List<SampleResponseDTO> getSamplesByStatus(SampleStatus status);

    List<SampleResponseDTO> getSamplesByStudy(Long studyId);

    ApiResponseDTO getSampleFull(Long sampleId);
}