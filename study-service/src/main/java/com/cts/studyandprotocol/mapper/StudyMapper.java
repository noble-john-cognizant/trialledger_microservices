package com.cts.studyandprotocol.mapper;

import com.cts.studyandprotocol.dto.StudyRequestDto;
import com.cts.studyandprotocol.dto.StudyResponseDto;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.model.StudyStatus;
import org.springframework.stereotype.Component;

@Component
public class StudyMapper {

    public Study toEntity(StudyRequestDto dto) {
        Study study = new Study();
        study.setTitle(dto.getTitle());
        study.setSponsor(dto.getSponsor());
        study.setProtocolNumber(dto.getProtocolNumber());
        study.setStartDate(dto.getStartDate());
        study.setEndDate(dto.getEndDate());
        study.setStatus(StudyStatus.PLANNED);
        study.setIsDeleted(false);
        return study;
    }

    public StudyResponseDto toResponse(Study study) {
        StudyResponseDto dto = new StudyResponseDto();
        dto.setStudyId(study.getStudyId());
        dto.setTitle(study.getTitle());
        dto.setSponsor(study.getSponsor());
        dto.setProtocolNumber(study.getProtocolNumber());
        dto.setStartDate(study.getStartDate());
        dto.setEndDate(study.getEndDate());
        dto.setStatus(study.getStatus());
        dto.setIsDeleted(study.getIsDeleted());
        return dto;
    }
}
