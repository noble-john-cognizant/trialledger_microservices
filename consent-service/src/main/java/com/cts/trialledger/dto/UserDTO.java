package com.cts.trialledger.dto;

/**
 * Mirror of the auth-service UserDTO. Used by {@code AuthClient.getUserById}
 * to look up the logged-in user's identity (phone) when enforcing
 * participant-level access control on consents.
 */
public record UserDTO(
        Long userId,
        String name,
        String role,
        String email,
        String phone,
        String status,
        java.time.LocalDateTime createdAt
) {}
