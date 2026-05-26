package com.cts.trialledger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password must be present")
        @Size(min = 8, max = 50)
        String password,

        @Size(min = 10, max = 10)
        @NotBlank(message = "Phone number must be present")
        String phone,

        @NotBlank(message = "Name cannot blank")
        String name
) {
}
