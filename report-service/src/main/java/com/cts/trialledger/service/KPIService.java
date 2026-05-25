package com.cts.trialledger.service;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface KPIService {

    KPIResponseDTO createKPI(KPIRequestDTO dto) throws JsonProcessingException;

    KPIResponseDTO getKPIById(Long id);

    List<KPIResponseDTO> getAllKPIs();

    List<KPIResponseDTO> getKPIsByPeriod(String period);

}