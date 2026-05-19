package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.entity.KPI;
import org.springframework.stereotype.Component;

@Component
public class KPIMapper {

    public KPI toEntity(KPIRequestDTO dto) {
        return KPI.builder()
                .name(dto.getName())
                .definition(dto.getDefinition())
                .target(dto.getTarget())
                .currentValue(0.0)
                .reportingPeriod(dto.getReportingPeriod())
                .build();
    }

    public KPIResponseDTO toResponse(KPI k) {
        return KPIResponseDTO.builder()
                .kpiId(k.getKpiId())
                .name(k.getName())
                .definition(k.getDefinition())
                .target(k.getTarget())
                .currentValue(k.getCurrentValue())
                .reportingPeriod(k.getReportingPeriod())
                .build();
    }
}