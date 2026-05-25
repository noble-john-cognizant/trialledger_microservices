package com.cts.trialledger.dto;

import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.model.SampleType;
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
    private SampleType sampleType;
    private LocalDateTime collectedAt;
    private String collectedBy;
    private String initialLocation;
    private SampleStatus status;
}