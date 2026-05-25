package com.cts.trialledger.provenance.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ConsentClientFallback implements ConsentClient {
    @Override
    public List<Map<String, Object>> getConsentRecordsByStudy(Long studyId) {
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> getParticipantsByStudy(Long studyId) {
        return Collections.emptyList();
    }
}
