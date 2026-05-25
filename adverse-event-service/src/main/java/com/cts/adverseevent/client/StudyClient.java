package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.StudyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "STUDY-SERVICE",
        fallback = StudyClientFallback.class)
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    StudyDto getStudyById(@PathVariable("studyId") Long studyId);
}
