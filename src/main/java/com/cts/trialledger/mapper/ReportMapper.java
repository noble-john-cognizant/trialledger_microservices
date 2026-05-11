package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponseDTO toResponse(Report report) {
        ReportResponseDTO dto = ReportResponseDTO.builder()
                .reportId(report.getReportId())
                .studyId(report.getStudyId())
                .scope(report.getScope())
                .metricsJson(report.getMetricsJson())
                .generatedAt(report.getGeneratedAt())
                .reportUri(report.getReportUri())
                .build();

        return dto;
    }
}