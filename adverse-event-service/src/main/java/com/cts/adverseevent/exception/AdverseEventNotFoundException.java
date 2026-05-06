package com.cts.adverseevent.exception;

public class AdverseEventNotFoundException extends RuntimeException {
    public AdverseEventNotFoundException(Long aeId) {
        super("Adverse Event not found with ID: " + aeId);
    }
}
