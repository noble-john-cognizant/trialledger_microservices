package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.ProvenanceRequestDTO;
import com.cts.trialledger.client.dto.ProvenanceStatsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProvenanceClientFallback implements ProvenanceClient {
    @Override
    public ProvenanceStatsDTO getProvenanceStats(Long studyId) {
        return null;
    }

    @Override
    public ResponseEntity<String> recordProvenanceData(ProvenanceRequestDTO dto) {
        return ResponseEntity.status(503).body("Provenance service unavailable");
    }
}
