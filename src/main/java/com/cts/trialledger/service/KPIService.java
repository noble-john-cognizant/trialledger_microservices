package com.cts.trialledger.service;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;

import java.util.List;

public interface KPIService {

    KPIResponseDTO createKPI(KPIRequestDTO dto);

    KPIResponseDTO getKPIById(Long id);

    List<KPIResponseDTO> getAllKPIs();

    List<KPIResponseDTO> getKPIsByPeriod(String period);
}