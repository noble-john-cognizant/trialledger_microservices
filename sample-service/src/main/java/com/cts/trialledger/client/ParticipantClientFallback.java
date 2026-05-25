package com.cts.trialledger.client;

import com.cts.trialledger.dto.ParticipantResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipantClientFallback implements ParticipantClient {
    @Override
    public ParticipantResponseDTO getParticipantById(Long participantId) {
        return null;
    }
}
