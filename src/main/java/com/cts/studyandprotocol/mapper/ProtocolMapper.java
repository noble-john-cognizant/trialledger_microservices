package com.cts.studyandprotocol.mapper;

import com.cts.studyandprotocol.dto.ProtocolVersionRequestDto;
import com.cts.studyandprotocol.dto.ProtocolVersionResponseDto;
import com.cts.studyandprotocol.entity.ProtocolVersion;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.model.ProtocolStatus;
import org.springframework.stereotype.Component;

@Component
public class ProtocolMapper {

    public ProtocolVersion toEntity(ProtocolVersionRequestDto dto, Study study) {
        ProtocolVersion protocol = new ProtocolVersion();
        protocol.setStudy(study);
        protocol.setVersionNumber(dto.getVersionNumber());
        protocol.setDocumentUrl(dto.getDocumentUrl());
        protocol.setEffectiveDate(dto.getEffectiveDate());
<<<<<<< HEAD
=======
        protocol.setApprovedById(dto.getApprovedById());
>>>>>>> 09b0807c705d15c7b53b6bb00bf12bc6fa4e2ec9
        protocol.setStatus(ProtocolStatus.DRAFT);
        protocol.setIsDeleted(false);
        return protocol;
    }

    public ProtocolVersionResponseDto toResponse(ProtocolVersion protocol) {
        ProtocolVersionResponseDto dto = new ProtocolVersionResponseDto();
        dto.setProtocolId(protocol.getProtocolId());
        dto.setStudyId(protocol.getStudy().getStudyId());
        dto.setVersionNumber(protocol.getVersionNumber());
        dto.setDocumentUrl(protocol.getDocumentUrl());
        dto.setEffectiveDate(protocol.getEffectiveDate());
        dto.setApprovedById(protocol.getApprovedById());
        dto.setStatus(protocol.getStatus());
        dto.setIsDeleted(protocol.getIsDeleted());
        return dto;
    }
}
