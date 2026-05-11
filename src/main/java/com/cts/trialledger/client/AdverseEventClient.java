package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.AdverseEventStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "adverse-event-service", path = "/api/adverse-events")
public interface AdverseEventClient {

    @GetMapping("/stats/{studyId}")
    AdverseEventStatsDTO getAdverseEventStats(@PathVariable("studyId") Long studyId);
}