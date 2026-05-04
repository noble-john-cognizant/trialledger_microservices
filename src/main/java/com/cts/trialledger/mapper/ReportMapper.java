package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponseDTO toResponse(Report r) {
        return ReportResponseDTO.builder()
                .reportId(r.getReportId())
                .studyId(r.getStudyId())
                .scope(r.getScope())
                .metricsJson(r.getMetricsJson())
                .generatedAt(r.getGeneratedAt())
                .reportUri(r.getReportUri())
                .build();
    }
}