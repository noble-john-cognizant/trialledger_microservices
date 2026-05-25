package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.ParticipantDto;
import org.springframework.stereotype.Component;

@Component
public class ParticipantClientFallback implements ParticipantClient {
    @Override
    public ParticipantDto getParticipantById(Long id) {
        return null;
    }
}
