package com.cts.trialledger.auth.exception;

/**
 * Thrown when a password-reset OTP fails validation (wrong code, expired,
 * never requested, or too many attempts).
 */
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
