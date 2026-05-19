package com.cts.trialledger.apigateway.dto;

import java.time.LocalDateTime;

public record LoginResponseDTO(
        String name,
        String accessToken,
        String role,
        Long userId,
        String status,
        LocalDateTime createdAt
) {

}
