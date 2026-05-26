package com.cts.trialledger.client;

import com.cts.trialledger.dto.RegisterDTO;
import com.cts.trialledger.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthClientFallback implements AuthClient {

    @Override
    public ResponseEntity<String> register(RegisterDTO dto) {
        return ResponseEntity.status(503).body("Auth service unavailable");
    }

    @Override
    public UserDTO getUserById(Long userId) {
        // Auth-service is down — we can't safely identify the caller,
        // so signal "unknown" by returning null. Callers MUST handle it.
        return null;
    }
}
