package com.cts.trialledger.provenance.client.fallback;

import com.cts.trialledger.provenance.client.ConsentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link ConsentClient}. Returned by Spring Cloud's circuit
 * breaker integration when CONSENT-SERVICE is degraded or the CB is OPEN.
 * Returning empty lists lets the snapshot/audit pipeline continue with
 * partial data instead of failing the whole package generation.
 */
@Slf4j
@Component
public class ConsentClientFallback implements ConsentClient {

    @Override
    public List<Map<String, Object>> getConsentRecordsByStudy(Long studyId) {
        log.warn("[CB-FALLBACK] CONSENT-SERVICE unavailable — returning empty consent records for studyId={}", studyId);
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getParticipantsByStudy(Long studyId) {
        log.warn("[CB-FALLBACK] CONSENT-SERVICE unavailable — returning empty participants for studyId={}", studyId);
        return Collections.emptyList();
    }
}
