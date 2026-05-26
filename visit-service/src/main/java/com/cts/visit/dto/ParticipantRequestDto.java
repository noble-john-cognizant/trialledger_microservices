package com.cts.visit.dto;

/**
 * Local DTO mirroring participant-service's request shape.
 * Only participantId for now — extend when participant-service contract is finalized.
 */
public class ParticipantRequestDto {

    private Long participantId;

    public ParticipantRequestDto() {
    }

    public ParticipantRequestDto(Long participantId) {
        this.participantId = participantId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
