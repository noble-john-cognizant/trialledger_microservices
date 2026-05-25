package com.cts.adverseevent.exception;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(Long participantId) {
        super("Participant with ID " + participantId + " does not exist");
    }
}
