package com.cts.trialledger.client;

import com.cts.trialledger.dto.StudyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class StudyClientFallback implements StudyClient {
    @Override
    public StudyResponseDTO getStudyById(Long studyId) {
        return null;
    }
}
