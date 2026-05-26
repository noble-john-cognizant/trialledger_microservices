package com.cts.notificationservice.client;

import com.cts.notificationservice.dto.StudyDTO;
import org.springframework.stereotype.Component;

@Component
public class StudyClientFallback implements StudyClient {
    @Override
    public StudyDTO getStudyById(Long studyId) {
        return null;
    }
}
