package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleResponseDTO {

    private Long sampleId;
    private Long participantId;
    private Long studyId;
    private String sampleType;
    private LocalDateTime collectedAt;
    private String collectedBy;
    private String initialLocation;
    private String status;

    private Long totalSamples;
    private Long collectedCount;
    private Long inAnalysisCount;
    private Long completedCount;
    private Long custodyEventCount;
    private Long assayRunCount;

}