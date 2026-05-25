package com.cts.trialledger.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Step 2 of the password-reset flow — submit the OTP shown on the auth-service
 * console along with the new password. The OTP is validated server-side before
 * the password is updated.
 */
public record ForgotPasswordDTO(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
        String otp,

        @NotBlank(message = "New password must be present")
        @Size(min = 8, max = 50, message = "Password must be 8-50 characters")
        String newPassword
) {
}
