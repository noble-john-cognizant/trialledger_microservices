package com.cts.studyandprotocol.client;


import com.cts.studyandprotocol.dto.ProvenanceRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PROVENANCE-SERVICE", path = "/api/provenance",
        fallback = ProvenanceClientFallback.class)
public interface ProvenanceClient {
    @PostMapping
    ResponseEntity<String> recordProvenanceData(@RequestBody ProvenanceRequestDTO dto);
}