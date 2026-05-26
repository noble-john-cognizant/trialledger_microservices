package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.AdverseEventStatsDTO;
import org.springframework.stereotype.Component;

@Component
public class AdverseEventClientFallback implements AdverseEventClient {
    @Override
    public AdverseEventStatsDTO getAdverseEventStats(Long studyId) {
        return null;
    }
}
