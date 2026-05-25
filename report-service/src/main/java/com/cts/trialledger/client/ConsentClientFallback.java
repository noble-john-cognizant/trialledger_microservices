package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.EnrollmentStatsDTO;
import org.springframework.stereotype.Component;

@Component
public class ConsentClientFallback implements ConsentClient {
    @Override
    public EnrollmentStatsDTO getEnrollmentStats(Long studyId) {
        return null;
    }
}
