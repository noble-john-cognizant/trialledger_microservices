package com.cts.trialledger.provenance.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AdverseEventClientFallback implements AdverseEventClient {
    @Override
    public ResponseEntity<List<Map<String, Object>>> getAdverseEventByStudy(Long studyId) {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
