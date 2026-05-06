package com.cts.trialledger.dto;

import com.cts.trialledger.model.ReportScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO {

    private Long reportId;
    private Long studyId;
    private ReportScope scope;
    private String metricsJson;
    private LocalDateTime generatedAt;
    private String reportUri;
}