package com.cts.trialledger.provenance.client;

import com.cts.trialledger.provenance.client.fallback.AdverseEventClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ADVERSE-EVENT-SERVICE",path = "/api/adverse-events",
        fallback = AdverseEventClientFallback.class)
public interface AdverseEventClient {
    @GetMapping("/study/{studyId}")
    ResponseEntity<List<Map<String,Object>>> getAdverseEventByStudy(@PathVariable Long studyId);

    }
