package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.ParticipantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "participant-service")
public interface ParticipantClient {

    @GetMapping("/api/participants/{id}")
    ParticipantDto getParticipantById(@PathVariable("id") Long id);
}
