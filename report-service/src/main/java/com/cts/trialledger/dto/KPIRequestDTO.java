package com.cts.trialledger.dto;

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
public class KPIRequestDTO {

    @NotBlank(message = "KPI name is required")
    private String name;

    @NotNull(message = "Study ID is required")
    private Long studyId;

    @NotBlank(message = "KPI definition is required")
    private String definition;

    @NotNull(message = "KPI target is required")
    private Double target;

    @NotBlank(message = "Reporting period is required")
    private String reportingPeriod;
}