package com.cts.adverseevent.mapper;

import com.cts.adverseevent.dto.AdverseEventRequestDto;
import com.cts.adverseevent.dto.AdverseEventResponseDto;
import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.model.AEStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdverseEventMapper {

    public AdverseEvent toEntity(AdverseEventRequestDto dto) {
        AdverseEvent ae = new AdverseEvent();
        ae.setParticipantId(dto.getParticipantId());
        ae.setStudyId(dto.getStudyId());
        ae.setReportedAt(LocalDateTime.now());
        ae.setSeverity(dto.getSeverity());
        ae.setDescription(dto.getDescription());
        ae.setReportedById(dto.getReportedById());
        ae.setStatus(AEStatus.OPEN);
        ae.setIsDeleted(false);
        return ae;
    }

    public AdverseEventResponseDto toResponse(AdverseEvent ae) {
        AdverseEventResponseDto dto = new AdverseEventResponseDto();
        dto.setAeId(ae.getId());
        dto.setParticipantId(ae.getParticipantId());
        dto.setStudyId(ae.getStudyId());
        dto.setReportedAt(ae.getReportedAt());
        dto.setSeverity(ae.getSeverity());
        dto.setDescription(ae.getDescription());
        dto.setReportedById(ae.getReportedById());
        dto.setStatus(ae.getStatus());
        dto.setIsDeleted(ae.getIsDeleted());
        return dto;
    }
}