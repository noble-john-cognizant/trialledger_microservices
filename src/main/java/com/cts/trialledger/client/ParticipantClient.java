package com.cts.trialledger.client;

import com.cts.trialledger.dto.ParticipantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "CONSENT-SERVICE")
public interface ParticipantClient {

    @GetMapping("/api/participants/{id}")
    ParticipantDto getParticipantById(@PathVariable("id") Long id);
}
