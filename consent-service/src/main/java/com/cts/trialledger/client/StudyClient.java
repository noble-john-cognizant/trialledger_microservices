package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@FeignClient(name = "STUDY-SERVICE",
contextId ="studyClient", url="http://localhost:8082/api/studies") // MUST match Eureka name
public interface StudyClient {

    @GetMapping("/{studyId}")
    Object getStudyById(@PathVariable("studyId") Long studyId);
}