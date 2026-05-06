package com.cts.trialledger.client;

import com.cts.trialledger.dto.SampleResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SAMPLE-SERVICE", path = "/api/samples")
public interface SampleClient {

    @GetMapping("/stats/{studyId}")
    SampleResponseDTO getSampleStats(@PathVariable("studyId") Long studyId);
}