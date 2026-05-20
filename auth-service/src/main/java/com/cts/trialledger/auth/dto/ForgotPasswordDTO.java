package com.cts.trialledger.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "New password must be present")
        String newPassword
) {
}
