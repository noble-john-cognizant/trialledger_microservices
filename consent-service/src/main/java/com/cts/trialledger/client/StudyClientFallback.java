package com.cts.trialledger.client;

import org.springframework.stereotype.Component;

@Component
public class StudyClientFallback implements StudyClient {
    @Override
    public Object getStudyById(Long studyId) {
        return null;
    }
}
