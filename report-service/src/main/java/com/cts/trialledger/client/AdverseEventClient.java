package com.cts.trialledger.client;

import com.cts.trialledger.dto.AdverseEventResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ADVERSE-EVENTS-SERVICE", path = "/api/adverse-events")
public interface AdverseEventClient {

    @GetMapping("/stats/{studyId}")
    AdverseEventResponseDTO getAdverseEventStats(@PathVariable("studyId") Long studyId);
}