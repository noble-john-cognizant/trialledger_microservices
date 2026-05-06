package com.cts.trialledger.client;

import com.cts.trialledger.dto.ProvenanceDTO;
import com.cts.trialledger.dto.ProvenanceRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "provenance-service", path = "/api/provenance")
public interface ProvenanceClient {

    @GetMapping("/stats/{studyId}")
    ProvenanceDTO getProvenanceStats(@PathVariable("studyId") Long studyId);

    @PostMapping
    ResponseEntity<String> recordProvenanceData(@RequestBody ProvenanceRequestDTO dto);
}