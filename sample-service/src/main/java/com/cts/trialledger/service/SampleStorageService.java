package com.cts.trialledger.service;

import com.cts.trialledger.dto.SampleStorageRequestDTO;
import com.cts.trialledger.dto.SampleStorageResponseDTO;

import java.util.List;

public interface SampleStorageService {

    SampleStorageResponseDTO storeSample(Long sampleId, SampleStorageRequestDTO dto);

    SampleStorageResponseDTO retrieveSample(Long storageId);

    List<SampleStorageResponseDTO> getStorageHistory(Long sampleId);
}