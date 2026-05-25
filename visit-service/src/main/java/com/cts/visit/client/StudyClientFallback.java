package com.cts.visit.client;

import com.cts.visit.dto.StudyResponseDto;
import org.springframework.stereotype.Component;

@Component
public class StudyClientFallback implements StudyClient {
    @Override
    public StudyResponseDto getStudyById(Long studyId) {
        return null;
    }
}
