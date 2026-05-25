package com.cts.trialledger.service;

import com.cts.trialledger.dto.ChainOfCustodyRequestDTO;
import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;

import java.util.List;

public interface ChainOfCustodyService {

    ChainOfCustodyResponseDTO transferCustody(Long sampleId, ChainOfCustodyRequestDTO requestDTO);

    ChainOfCustodyResponseDTO getCustodyById(Long cocId);

    List<ChainOfCustodyResponseDTO> getCustodyBySampleId(Long sampleId);

    ChainOfCustodyResponseDTO getLatestCustody(Long sampleId);
}