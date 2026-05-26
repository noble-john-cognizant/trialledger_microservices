package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.*;

public class ConsentMapper {

    public static ConsentRecord toEntity(ConsentRequestDTO dto, Participant p) {
        return ConsentRecord.builder()
                .participantId(p)
                .protocolId(dto.getProtocolId())
                .versionNumber(dto.getVersionNumber())
                .consentMethod(dto.getConsentMethod())
                .signedDocumentUri(dto.getSignedDocumentUri())
                .build();
    }

    public static ConsentResponseDTO toResponse(ConsentRecord c) {
        return ConsentResponseDTO.builder()
                .consentId(c.getConsentId())
                .participantId(c.getParticipantId().getParticipantId())
                .protocolId(c.getProtocolId())
                .versionNumber(c.getVersionNumber())
                .status(c.getStatus().name())
                .consentDate(c.getConsentDate())
                .consentMethod(c.getConsentMethod().name())
                .signedDocumentUri(c.getSignedDocumentUri())
                .build();
    }
}