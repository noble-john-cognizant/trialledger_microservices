package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.SampleStatsDTO;
import org.springframework.stereotype.Component;

@Component
public class SampleClientFallback implements SampleClient {
    @Override
    public SampleStatsDTO getSampleStats(Long studyId) {
        return null;
    }
}
