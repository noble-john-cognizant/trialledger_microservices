package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.AdverseEventStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ADVERSE-EVENTS-SERVICE", path = "/api/adverse-events",
        fallback = AdverseEventClientFallback.class)
public interface AdverseEventClient {

    @GetMapping("/stats/{studyId}")
    AdverseEventStatsDTO getAdverseEventStats(@PathVariable("studyId") Long studyId);
}