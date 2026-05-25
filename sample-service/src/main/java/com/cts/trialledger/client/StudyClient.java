package com.cts.trialledger.client;

import com.cts.trialledger.dto.StudyResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "STUDY-SERVICE", path = "/api/studies",
        fallback = StudyClientFallback.class)
public interface StudyClient {
    @GetMapping("/{studyId}")
    StudyResponseDTO getStudyById(@PathVariable("studyId") Long studyId);
}