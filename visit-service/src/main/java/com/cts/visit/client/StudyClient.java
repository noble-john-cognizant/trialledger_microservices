package com.cts.visit.client;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.dto.StudyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for study-service.
 *
 * The `name` MUST match the spring.application.name registered on Eureka
 * by study-service (i.e. "study-service").
 *
 * Adjust the path inside @GetMapping if study-service exposes the endpoint
 * under a different URL.
 */
@FeignClient(name = "study-service")
public interface StudyClient {

    @GetMapping("/api/studies/{studyId}")
    ApiResponseDto<StudyResponseDto> getStudyById(@PathVariable("studyId") Long studyId);
}
