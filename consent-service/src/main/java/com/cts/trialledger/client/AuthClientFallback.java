package com.cts.trialledger.client;

import com.cts.trialledger.dto.RegisterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthClientFallback implements AuthClient {
    @Override
    public ResponseEntity<String> register(RegisterDTO dto) {
        return ResponseEntity.status(503).body("Auth service unavailable");
    }
}
