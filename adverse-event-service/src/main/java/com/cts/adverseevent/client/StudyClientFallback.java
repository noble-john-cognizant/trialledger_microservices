package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.StudyDto;
import org.springframework.stereotype.Component;

@Component
public class StudyClientFallback implements StudyClient {
    @Override
    public StudyDto getStudyById(Long studyId) {
        return null;
    }
}
