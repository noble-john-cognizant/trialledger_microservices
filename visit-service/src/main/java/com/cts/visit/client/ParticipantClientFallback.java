package com.cts.visit.client;

import com.cts.visit.dto.ParticipantResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ParticipantClientFallback implements ParticipantClient {
    @Override
    public ParticipantResponseDto getParticipantById(Long participantId) {
        return null;
    }
}
