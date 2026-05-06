package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KPIResponseDTO {

    private Long kpiId;
    private String name;
    private String definition;
    private Double target;
    private Double currentValue;
    private String reportingPeriod;
}