package com.cts.trialledger.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        String name,
        @Email(message = "Invalid email format")
        String email,
        @Size(min = 10, max = 10)
        String phone
) {
}
