package com.cts.trialledger.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ForgotUsernameDTO(
        @NotBlank(message = "Phone number must be present")
        String phoneNumber,
        @NotBlank(message = "Password must be present")
        String password) {
}
