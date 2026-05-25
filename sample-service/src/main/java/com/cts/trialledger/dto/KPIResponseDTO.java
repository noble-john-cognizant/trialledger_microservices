package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KPIResponseDTO {
    private Long kpiId;
    private Long studyId;
    private String reportingPeriod;
}