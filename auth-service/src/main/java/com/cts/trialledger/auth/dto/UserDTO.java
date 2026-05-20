package com.cts.trialledger.auth.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Long userId,
        String name,
        String role,
        String email,
        String phone,
        String status,
        LocalDateTime createdAt
) {
}
