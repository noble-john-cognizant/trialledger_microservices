package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.dto.ProvenanceRequestDTO;
import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.entity.KPI;
import com.cts.trialledger.exception.KPINotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.KPIMapper;
import com.cts.trialledger.repository.KPIRepository;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import com.cts.trialledger.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final KPIRepository kpiRepository;
    private final KPIMapper kpiMapper;
    private final ProvenanceClient provenanceClient;

    @Override
    public KPIResponseDTO createKPI(KPIRequestDTO dto) throws JsonProcessingException {
        KPI kpi = KPI.builder()
                .name(dto.getName())
                .definition(dto.getDefinition())
                .target(dto.getTarget())
                .currentValue(0.0)
                .reportingPeriod(dto.getReportingPeriod())
                .build();
        KPI saved = kpiRepository.save(kpi);

        //Record
        Map<String, Object> map = Map.of("name", saved.getName(), "targetValue", saved.getTarget(), "currentValue", saved.getCurrentValue());
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_KPI", "kpi", UserUtil.getCurrentUserId(), saved.getKpiId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(requestDTO);

        return kpiMapper.toResponse(saved);
    }

    @Override
    public KPIResponseDTO getKPIById(Long id) {
        KPIResponseDTO response = kpiMapper.toResponse(kpiRepository.findById(id).orElseThrow(() -> new KPINotFoundException(id)));

        return response;
    }

    @Override
    public List<KPIResponseDTO> getAllKPIs() {
        List<KPIResponseDTO> response = kpiRepository.findAll().stream().map(kpiMapper::toResponse).toList();

        return response;
    }

    @Override
    public List<KPIResponseDTO> getKPIsByPeriod(String period) {
        List<KPIResponseDTO> kpis = kpiRepository.findByReportingPeriod(period).stream().map(kpiMapper::toResponse).toList();

        if (kpis.isEmpty()) {
            throw new ResourceNotFoundException("No KPIs found for reporting period: " + period);
        }
        return kpis;
    }
}