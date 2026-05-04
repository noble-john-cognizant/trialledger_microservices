package com.cts.trialledger.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdverseEventResponseDTO
{
    private Long aeId;
    private Long participantId;
    private Long studyId;
    private LocalDateTime reportedAt;
    private String severity;
    private String description;
    private String reportedBy;
    private String Status;
    private Boolean isDeleted;


    private Long totalEvents;
    private Long mildCount;
    private Long moderateCount;
    private Long severeCount;
    private Long lifeThreatenigCount;
}