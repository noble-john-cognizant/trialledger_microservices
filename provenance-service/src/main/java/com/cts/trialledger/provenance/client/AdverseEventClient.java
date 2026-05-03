package com.cts.trialledger.provenance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "ADVERSE-EVENT-SERVICE")
public interface AdverseEventClient {
    @GetMapping
    List<Map<String,Object>> getAdverseEventByStudy(@PathVariable Long studyId);
}
