package com.cts.adverseevent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdverseEventStatsDTO {
    private Long studyId;
    private Long totalEvents;
    private Long mildCount;
    private Long moderateCount;
    private Long severeCount;
}