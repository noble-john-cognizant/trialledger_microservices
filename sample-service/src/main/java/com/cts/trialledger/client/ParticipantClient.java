package com.cts.trialledger.client;

import com.cts.trialledger.dto.ParticipantResponseDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CONSENT-SERVICE", path = "/api/participants")
public interface ParticipantClient {

    @GetMapping("/{participantId}")
    ParticipantResponseDTO getParticipantById(@PathVariable("participantId") Long participantId);
}