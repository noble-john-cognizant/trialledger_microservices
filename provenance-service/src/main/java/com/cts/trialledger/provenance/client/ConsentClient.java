package com.cts.trialledger.provenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "CONSENT-SERVICE", path = "/api")
public interface ConsentClient {
    // Find all consent client by study id
    @GetMapping("/consents/study/{studyId}")
    List<Map<String, Object>> getConsentRecordsByStudy(@PathVariable Long studyId);

    // Find all participant by study id
    @GetMapping("/participants/study/{studyId}")
    List<Map<String,Object>> getParticipantsByStudy(@PathVariable Long studyId);
}
