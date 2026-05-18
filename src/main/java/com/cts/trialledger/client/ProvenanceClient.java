package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.ProvenanceRequestDTO;
import com.cts.trialledger.client.dto.ProvenanceStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PROVENANCE-SERVICE", path = "/api/provenance")
public interface ProvenanceClient {

    @GetMapping("/stats/{studyId}")
    ProvenanceStatsDTO getProvenanceStats(@PathVariable("studyId") Long studyId);

    @PostMapping
    ResponseEntity<String> recordProvenanceData(@RequestBody ProvenanceRequestDTO dto);
}