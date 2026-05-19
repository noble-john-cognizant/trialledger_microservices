package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleStatsDTO {

    private Long studyId;
    private Long totalSamples;
    private Long collectedCount;
    private Long inAnalysisCount;
    private Long completedCount;
    private Long custodyEventCount;
    private Long assayRunCount;
}
