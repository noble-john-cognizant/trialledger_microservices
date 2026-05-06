package com.cts.visit.mapper;

import com.cts.visit.dto.VisitRequestDto;
import com.cts.visit.dto.VisitResponseDto;
import com.cts.visit.entity.Visit;

public class VisitMapper {

    public static Visit toEntity(VisitRequestDto request) {
        Visit visit = new Visit();
        visit.setParticipantId(request.getParticipantId());
        visit.setStudyId(request.getStudyId());
        visit.setVisitType(request.getVisitType());
        visit.setScheduledAt(request.getScheduledAt());
        return visit;
    }

    public static VisitResponseDto toResponseDto(Visit visit) {
        VisitResponseDto dto = new VisitResponseDto();
        dto.setVisitId(visit.getVisitId());
        dto.setParticipantId(visit.getParticipantId());
        dto.setStudyId(visit.getStudyId());
        dto.setVisitType(visit.getVisitType());
        dto.setScheduledAt(visit.getScheduledAt());
        dto.setPerformedAt(visit.getPerformedAt());
        dto.setStatus(visit.getStatus());
        return dto;
    }
}
