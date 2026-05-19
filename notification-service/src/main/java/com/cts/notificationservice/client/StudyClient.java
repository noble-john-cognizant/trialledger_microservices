package com.cts.notificationservice.client;

import com.cts.notificationservice.dto.StudyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "study-service")
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    StudyDTO getStudyById(@PathVariable Long studyId);
}
