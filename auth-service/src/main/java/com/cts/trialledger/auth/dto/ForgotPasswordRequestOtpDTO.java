package com.cts.trialledger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Step 1 of the password-reset flow — request an OTP for the supplied email. */
public record ForgotPasswordRequestOtpDTO(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email
) {}
