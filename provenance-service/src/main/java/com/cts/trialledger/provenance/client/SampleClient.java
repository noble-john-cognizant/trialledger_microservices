package com.cts.trialledger.provenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(value = "SAMPLE-SERVICE",path = "/api/samples")
public interface SampleClient {
    @GetMapping("/study/{studyId}")
    List<Map<String, Object>> getSamplesByStudy(@PathVariable Long studyId);

}
