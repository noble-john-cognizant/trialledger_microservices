package com.cts.trialledger.dto;

import com.cts.trialledger.model.ReportScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {

    @NotNull(message = "studyId is required")
    private Long studyId;

    @NotNull(message = "scope is required")
    private ReportScope scope;

    private String parametersJson;

    @NotBlank(message = "reportingPeriod is required")
    private String reportingPeriod;
}