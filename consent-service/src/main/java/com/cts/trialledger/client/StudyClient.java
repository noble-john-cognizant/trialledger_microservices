package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "studyandprotocol-service",
contextId ="studyClient") // MUST match Eureka name
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    Object getStudyById(@PathVariable("studyId") Long studyId);
}