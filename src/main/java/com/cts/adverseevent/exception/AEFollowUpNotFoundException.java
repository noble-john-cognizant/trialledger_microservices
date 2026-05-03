package com.cts.adverseevent.exception;

public class AEFollowUpNotFoundException extends RuntimeException {
    public AEFollowUpNotFoundException(Long followUpId) {
        super("Follow Up not found with ID: " + followUpId);
    }
}
