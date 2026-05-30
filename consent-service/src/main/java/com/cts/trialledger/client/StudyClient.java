package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@FeignClient(name = "STUDY-SERVICE", path = "/api/studies",
        fallback = StudyClientFallback.class)
public interface StudyClient {

    @GetMapping("/{studyId}")
    Object getStudyById(@PathVariable("studyId") Long studyId);
}