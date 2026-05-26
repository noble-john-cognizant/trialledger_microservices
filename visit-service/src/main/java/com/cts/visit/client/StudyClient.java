package com.cts.visit.client;

import com.cts.visit.dto.StudyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "study-service",
        fallback = StudyClientFallback.class)
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    StudyResponseDto getStudyById(@PathVariable("studyId") Long studyId);
}
