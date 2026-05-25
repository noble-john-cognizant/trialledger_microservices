package com.cts.trialledger.client;

import com.cts.trialledger.dto.RegisterDTO;
import com.cts.trialledger.dto.UserDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE",
        fallback = AuthClientFallback.class)
public interface AuthClient {

    // The public self-service /api/auth/register endpoint was removed.
    // Participant enrollment now uses the internal-only path on AUTH-SERVICE
    // that the gateway does not proxy. Reachable only over Eureka
    // service-to-service load balancing; the gateway header propagation in
    // FeignContextPropagation supplies the calling coordinator's identity.
    @PostMapping("/api/auth/internal/register")
    ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto);

    /**
     * Fetch a user record by id. The auth-service endpoint is gated with
     * {@code @PreAuthorize("#userId == authentication.principal")}, so this
     * call only succeeds when the calling principal IS that user — exactly
     * the case for a participant looking up themselves.
     */
    @GetMapping("/api/users/{userId}")
    UserDTO getUserById(@PathVariable Long userId);
}
