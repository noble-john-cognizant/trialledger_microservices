package com.cts.trialledger.dto;

import com.cts.trialledger.model.ReportScope;
import com.fasterxml.jackson.annotation.JsonRawValue;
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

    @JsonRawValue
    private String metricsJson;
    private LocalDateTime generatedAt;
    private String reportUri;
}