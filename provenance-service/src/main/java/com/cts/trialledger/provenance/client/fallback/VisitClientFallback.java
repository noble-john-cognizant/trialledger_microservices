package com.cts.trialledger.provenance.client.fallback;

import com.cts.trialledger.provenance.client.VisitClient;
import com.cts.trialledger.provenance.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fallback for {@link VisitClient}. VISIT-SERVICE wraps every response in
 * its own {@link ApiResponseDto}, so the fallback rebuilds that envelope
 * with an empty data payload — the caller's `.getBody().getData()` chain
 * stays NPE-safe when the CB is open.
 */
@Slf4j
@Component
public class VisitClientFallback implements VisitClient {

    @Override
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getVisitsByStudy(Long studyId) {
        log.warn("[CB-FALLBACK] VISIT-SERVICE unavailable — returning empty visits for studyId={}", studyId);
        ApiResponseDto<List<Map<String, Object>>> body = new ApiResponseDto<>();
        body.setStatus("FAILURE");
        body.setMessage("Visit service unavailable — circuit breaker open.");
        body.setData(Collections.emptyList());
        return ResponseEntity.ok(body);
    }
}
