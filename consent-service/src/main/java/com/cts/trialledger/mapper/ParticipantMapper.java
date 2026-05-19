package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.Participant;

public class ParticipantMapper {

    public static Participant toEntity(ParticipantRequestDTO dto) {
        return Participant.builder()
                .studyId(dto.getStudyId())
                .externalId(dto.getExternalId())
                .name(dto.getName())
                .dob(dto.getDob())
                .contactInfo(dto.getContactInfo())
                .build();
    }

    public static ParticipantResponseDTO toResponse(Participant p) {
        return ParticipantResponseDTO.builder()
                .participantId(p.getParticipantId())
                .studyId(p.getStudyId())
                .externalId(p.getExternalId())
                .name(p.getName())
                .dob(p.getDob())
                .contactInfo(p.getContactInfo())
                .enrollmentStatus(p.getEnrollmentStatus())
                .build();
    }
}