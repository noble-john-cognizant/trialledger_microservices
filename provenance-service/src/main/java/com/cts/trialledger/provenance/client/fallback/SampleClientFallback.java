package com.cts.trialledger.provenance.client.fallback;

import com.cts.trialledger.provenance.client.SampleClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link SampleClient}. Returns an empty sample list so audit
 * packages and snapshots can still be generated when SAMPLE-SERVICE is down.
 */
@Slf4j
@Component
public class SampleClientFallback implements SampleClient {

    @Override
    public List<Map<String, Object>> getSamplesByStudy(Long studyId) {
        log.warn("[CB-FALLBACK] SAMPLE-SERVICE unavailable — returning empty samples for studyId={}", studyId);
        return Collections.emptyList();
    }
}
