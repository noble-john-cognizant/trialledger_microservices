package com.cts.trialledger.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AEFollowUpResponseDto {
    private Long followUpId;
    private Long aeId;
    private String actionTaken;
    private Long performedById;
    private LocalDateTime performedAt;
    private String notes;
    private Boolean isDeleted;
}
