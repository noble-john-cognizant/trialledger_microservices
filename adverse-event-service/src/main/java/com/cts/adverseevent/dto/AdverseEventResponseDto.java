package com.cts.adverseevent.dto;

import com.cts.adverseevent.model.AEStatus;
import com.cts.adverseevent.model.Severity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdverseEventResponseDto {
    private Long aeId;
    private Long participantId;
    private Long studyId;
    private LocalDateTime reportedAt;
    private Severity severity;
    private String description;
    private Long reportedById;
    private AEStatus status;
    private Boolean isDeleted;
}
