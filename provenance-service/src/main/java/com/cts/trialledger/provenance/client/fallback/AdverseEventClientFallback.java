package com.cts.trialledger.provenance.client.fallback;

import com.cts.trialledger.provenance.client.AdverseEventClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link AdverseEventClient}. Returns a 200 with an empty list
 * so downstream `.getBody()` calls don't NPE when the CB is open.
 */
@Slf4j
@Component
public class AdverseEventClientFallback implements AdverseEventClient {

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAdverseEventByStudy(Long studyId) {
        log.warn("[CB-FALLBACK] ADVERSE-EVENT-SERVICE unavailable — returning empty AEs for studyId={}", studyId);
        return ResponseEntity.ok(Collections.emptyList());
    }
}
