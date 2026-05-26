package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.SampleClient;
import com.cts.trialledger.client.dto.ProvenanceRequestDTO;
import com.cts.trialledger.client.dto.SampleStatsDTO;
import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.entity.KPI;
import com.cts.trialledger.exception.KPINotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.KPIMapper;
import com.cts.trialledger.repository.KPIRepository;
import com.cts.trialledger.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final KPIRepository kpiRepository;
    private final SampleClient sampleClient;
    private final KPIMapper kpiMapper;
    private final ProvenanceClient provenanceClient;

    @Override
    public KPIResponseDTO createKPI(KPIRequestDTO dto) throws JsonProcessingException {
        KPI kpi = kpiMapper.toEntity(dto);
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
        KPI kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new KPINotFoundException(id));
        SampleStatsDTO stats = sampleClient.getSampleStats(kpi.getStudyId());
        kpi.setCurrentValue(stats.getCollectedCount().doubleValue());
        return kpiMapper.toResponse(kpi);
    }

    @Override
    public List<KPIResponseDTO> getAllKPIs() {
        return kpiRepository.findAll().stream()
                .map(kpi -> {
                    try {
                        SampleStatsDTO stats = sampleClient.getSampleStats(kpi.getStudyId());
                        kpi.setCurrentValue(stats.getCollectedCount().doubleValue());
                    } catch (Exception e) {
                        log.warn("Could not fetch sample stats for studyId {}: {}", kpi.getStudyId(), e.getMessage());
                    }
                    return kpiMapper.toResponse(kpi);
                })
                .toList();
    }

    @Override
    public List<KPIResponseDTO> getKPIsByPeriod(String period) {
        List<KPI> kpis = kpiRepository.findByReportingPeriod(period);
        if (kpis.isEmpty()) {
            throw new ResourceNotFoundException("No KPIs found for reporting period: " + period);
        }
        return kpis.stream()
                .map(kpi -> {
                    try {
                        SampleStatsDTO stats = sampleClient.getSampleStats(kpi.getStudyId());
                        kpi.setCurrentValue(stats.getCollectedCount().doubleValue());
                    } catch (Exception e) {
                        log.warn("Could not fetch sample stats for studyId {}: {}", kpi.getStudyId(), e.getMessage());
                    }
                    return kpiMapper.toResponse(kpi);
                })
                .toList();
    }
}