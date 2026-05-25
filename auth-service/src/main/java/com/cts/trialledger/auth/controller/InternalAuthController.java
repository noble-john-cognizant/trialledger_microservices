package com.cts.trialledger.auth.controller;

import com.cts.trialledger.auth.dto.RegisterDTO;
import com.cts.trialledger.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal-only endpoints that are NOT proxied by the API gateway to the
 * outside world. They serve service-to-service calls made over Eureka
 * load-balanced Feign clients (see {@code consent-service AuthClient}).
 *
 * <p>The path prefix {@code /api/auth/internal/**} is intentionally
 * excluded from the gateway routes; only services on the internal network
 * can reach it. {@link PreAuthorize} adds an additional defence-in-depth
 * check: the caller must be at least a COORDINATOR (which is the case when
 * a coordinator enrolls a participant, propagated via the
 * {@code FeignContextPropagation} request interceptor).</p>
 */
@RestController
@RequestMapping("/api/auth/internal")
public class InternalAuthController {

    private final AuthService authService;

    public InternalAuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Create a User row for a freshly enrolled participant. Called only
     * from {@code consent-service ParticipantService#createParticipant} via
     * Feign. The public self-service register endpoint was removed.
     */
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR')")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto.name() + " register successfully");
    }
}
