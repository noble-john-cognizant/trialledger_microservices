package com.cts.trialledger.client;

import com.cts.trialledger.dto.StudyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "STUDY-SERVICE")
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    StudyDto getStudyById(@PathVariable("studyId") Long studyId);
}
