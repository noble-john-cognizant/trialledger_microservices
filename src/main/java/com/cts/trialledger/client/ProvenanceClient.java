package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.ProvenanceStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "provenance-service", path = "/api/provenance")
public interface ProvenanceClient {

    @GetMapping("/stats/{studyId}")
    ProvenanceStatsDTO getProvenanceStats(@PathVariable("studyId") Long studyId);
}