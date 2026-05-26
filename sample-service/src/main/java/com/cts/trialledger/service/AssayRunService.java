package com.cts.trialledger.service;

import com.cts.trialledger.dto.AssayRunRequestDTO;
import com.cts.trialledger.dto.AssayRunResponseDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface AssayRunService {

    List<AssayRunResponseDTO> getAllAssayRuns();

    AssayRunResponseDTO getAssayRunById(Long assayId);

    AssayRunResponseDTO createAssayRun(AssayRunRequestDTO requestDTO);

    List<AssayRunResponseDTO> getAssaysBySample(Long sampleId);

    List<AssayRunResponseDTO> getAssaysByOperator(Long operatorId);

    List<AssayRunResponseDTO> getAssaysByInstrument(Long instrumentId);

    Resource downloadResult(Long assayId);
}