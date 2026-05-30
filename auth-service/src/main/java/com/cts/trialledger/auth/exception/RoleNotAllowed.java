package com.cts.trialledger.auth.exception;

public class RoleNotAllowed extends RuntimeException {
    public RoleNotAllowed(String message) {
        super(message);
    }
}
