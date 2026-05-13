package com.cts.visit.dto;

import com.cts.visit.enums.VisitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class VisitRequestDto {

    @NotNull(message = "Participant ID is required")
    @Positive(message = "Participant ID must be a positive value")
    private Long participantId;

    @NotNull(message = "Study ID is required")
    @Positive(message = "Study ID must be a positive value")
    private Long studyId;

    @NotNull(message = "Visit type is required")
    private VisitType visitType;

    @NotNull(message = "Scheduled date and time is required")
    private LocalDateTime scheduledAt;

    public VisitRequestDto() {
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
