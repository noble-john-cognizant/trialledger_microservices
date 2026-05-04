package com.cts.trialledger.service;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.entity.KPI;
import com.cts.trialledger.exception.KPINotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.KPIMapper;
import com.cts.trialledger.repository.KPIRepository;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final KPIRepository kpiRepository;
    private final KPIMapper kpiMapper;
//    private final AuditService auditService;
//    private final ProvenanceRecordUtil provenanceRecordUtil;

    @Override
    public KPIResponseDTO createKPI(KPIRequestDTO dto) {
        KPI kpi = KPI.builder()
                .name(dto.getName())
                .definition(dto.getDefinition())
                .target(dto.getTarget())
                .currentValue(0.0)
                .reportingPeriod(dto.getReportingPeriod())
                .build();
        KPI saved = kpiRepository.save(kpi);

//        // Audit
//        auditService.storeAudit("CREATE_KPI", "kpi", "User ID: " + AuthValidator.getCurrentUserId() + " created kpi of id: " + saved.getKpiId());
//        //Record
//        Map<String, Object> map = Map.of("name", saved.getName(),
//                "targetValue", saved.getTarget(),
//                "currentValue", saved.getCurrentValue()
//        );
//        provenanceRecordUtil.saveProvenanceRecord("CREATE_KPI", "kpi", saved.getKpiId(), map);

        return kpiMapper.toResponse(saved);
    }

    @Override
    public KPIResponseDTO getKPIById(Long id) {
        KPIResponseDTO response = kpiMapper.toResponse(
                kpiRepository.findById(id)
                        .orElseThrow(() -> new KPINotFoundException(id))
        );

//        // Audit
//        auditService.storeAudit("VIEW_KPI", "kpi", "User ID: " + AuthValidator.getCurrentUserId() + " viewed kpi by id: " + response.getKpiId());

        return response;
    }

    @Override
    public List<KPIResponseDTO> getAllKPIs() {
        List<KPIResponseDTO> response = kpiRepository.findAll().stream()
                .map(kpiMapper::toResponse)
                .toList();

        // Audit
//        auditService.storeAudit("VIEW_KPI", "kpi", "User ID: " + AuthValidator.getCurrentUserId() + " viewed all KPIs");

        return response;
    }

    @Override
    public List<KPIResponseDTO> getKPIsByPeriod(String period) {
        List<KPIResponseDTO> kpis = kpiRepository.findByReportingPeriod(period)
                .stream().map(kpiMapper::toResponse).toList();

        if (kpis.isEmpty()) {
            throw new ResourceNotFoundException("No KPIs found for reporting period: " + period);
        }

        // Audit
//        auditService.storeAudit("VIEW_KPI", "kpi", "User ID: " + AuthValidator.getCurrentUserId() + " viewed kpi by reporting period: " + period);

        return kpis;
    }
}