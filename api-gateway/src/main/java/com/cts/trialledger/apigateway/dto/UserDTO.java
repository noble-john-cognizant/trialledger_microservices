package com.cts.trialledger.apigateway.dto;

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
