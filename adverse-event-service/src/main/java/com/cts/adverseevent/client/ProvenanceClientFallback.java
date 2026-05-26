package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.ProvenanceRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProvenanceClientFallback implements ProvenanceClient {
    @Override
    public ResponseEntity<String> recordProvenanceData(ProvenanceRequestDTO dto) {
        return ResponseEntity.status(503).body("Provenance service unavailable");
    }
}
