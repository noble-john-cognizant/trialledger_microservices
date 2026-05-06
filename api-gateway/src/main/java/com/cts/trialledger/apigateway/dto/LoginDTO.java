package com.cts.trialledger.apigateway.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @Email(message = "Invalid email format")
        @NotNull
        String email,
        @NotBlank(message = "Password must be present")
        @Size(min = 8,max = 50)
        String password
) {
}
