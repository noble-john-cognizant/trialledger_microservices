package com.cts.visit.dto;

/**
 * Local DTO used by visit-service's Feign client to deserialize
 * the response from participant-service.
 *
 * Only participantId is required for visit-service's needs.
 * Add more fields here later if participant-service returns them
 * and visit-service needs them.
 */
public class ParticipantResponseDto {

    private Long participantId;

    public ParticipantResponseDto() {
    }

    public ParticipantResponseDto(Long participantId) {
        this.participantId = participantId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
