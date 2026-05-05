package com.cts.visit.client;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.dto.ParticipantResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for participant-service.
 *
 * The `name` MUST match the spring.application.name registered on Eureka
 * by participant-service (i.e. "participant-service").
 */
@FeignClient(name = "CONSENT-SERVICE")
public interface ParticipantClient {

    @GetMapping("/api/participants/{Id}")
    ApiResponseDto<ParticipantResponseDto> getParticipantById(
            @PathVariable("Id") Long participantId);
}
