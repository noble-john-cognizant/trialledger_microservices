package com.cts.trialledger.service;

import com.cts.trialledger.client.NotificationClient;
import com.cts.trialledger.client.ProtocolClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.*;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.ConsentMapper;
import com.cts.trialledger.model.ConsentStatus;
import com.cts.trialledger.model.EnrollmentStatus;
import com.cts.trialledger.repository.*;
import com.cts.trialledger.util.HashUtil;
import com.cts.trialledger.util.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final ProvenanceClient provenanceClient;
    private final ParticipantRepository participantRepository;
    private final ConsentWithdrawalRepository withdrawalRepository;
    private final ProtocolClient protocolClient;
    private final NotificationClient notificationClient;

    public ConsentResponseDTO createConsent(ConsentRequestDTO dto) throws Exception {

        log.info("Creating consent for participantId: {}", dto.getParticipantId());

        // Validate participant
        Participant p = participantRepository.findById(dto.getParticipantId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Participant not found with ID: " + dto.getParticipantId())
                );

        // BLOCK withdrawn participants
        if (p.getEnrollmentStatus() == EnrollmentStatus.WITHDRAWN) {
            throw new RuntimeException("Cannot create consent for withdrawn participant");
        }

        // BLOCK completed participants
        if (p.getEnrollmentStatus() == EnrollmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot create consent for completed participant");
        }

        // CHECK ACTIVE CONSENT
        boolean activeExists = consentRepository
                .existsByParticipantId_ParticipantIdAndProtocolIdAndStatus(
                        dto.getParticipantId(),
                        dto.getProtocolId(),
                        ConsentStatus.ACTIVE
                );

        if (activeExists) {
            throw new RuntimeException("Active consent already exists for this protocol");
        }

        // Handle protocolId and versionNumber exceptions
        try {
            protocolClient.getProtocolVersion(dto.getProtocolId());
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Protocol not found for protocolId: "
                            + dto.getProtocolId()
                            + " and version: "
                            + dto.getVersionNumber()
            );
        } catch (FeignException.Unauthorized ex) {
            throw new ResourceNotFoundException(
                    "Protocol not found for protocolId: "
                            + dto.getProtocolId()
                            + " and version: "
                            + dto.getVersionNumber()
            );
        }

        // AUTO ENROLL PARTICIPANT
        ConsentRecord c = ConsentMapper.toEntity(dto, p);
        c.setConsentDate(java.time.LocalDateTime.now());
        c.setSignatureHash(HashUtil.generateSHA256(dto.getSignedDocumentUri()));
        c.setStatus(ConsentStatus.ACTIVE);

        ConsentRecord saved = consentRepository.save(c);

        p.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
        participantRepository.save(p);

        log.info("Consent created successfully with ID: {}", saved.getConsentId());

        // Record provenance
        Map<String, Object> map = Map.of(
                "consentMethod", saved.getConsentMethod(),
                "status", saved.getStatus(),
                "participantId", saved.getParticipantId(),
                "protocolId", saved.getProtocolId()
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO(
                    "CREATE_CONSENT",
                    "consent_record",
                    UserUtil.getCurrentUserId(),
                    saved.getConsentId(),
                    mapper.writeValueAsString(map)
            );

            provenanceClient.recordProvenanceData(requestDTO);
        } catch (Exception e) {
            log.error("Error while recording provenance: {}", e.getMessage());
        }

        // Send notification
        try {
            notificationClient.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(UserUtil.getCurrentUserId())
                            .entityId(saved.getConsentId())
                            .message("Consent recorded successfully for Participant ID: "
                                    + saved.getParticipantId().getParticipantId()
                                    + " under Protocol ID: " + saved.getProtocolId())
                            .category("CONSENT")
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send consent notification: {}", e.getMessage());
        }

        return ConsentMapper.toResponse(saved);
    }

    public List<ConsentResponseDTO> getConsentsByParticipant(Long id) {

        log.info("Fetching consents for participantId: {}", id);

        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Participant not found with ID: " + id);
        }

        List<ConsentResponseDTO> consents = consentRepository
                .findByParticipantId_ParticipantId(id)
                .stream()
                .map(ConsentMapper::toResponse)
                .collect(Collectors.toList());

        if (consents.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No consents found for participant ID: " + id);
        }

        return consents;
    }

    public String withdrawConsent(ConsentWithdrawalDTO dto) {

        log.info("Withdrawing consent ID: {}", dto.getConsentId());

        if (withdrawalRepository.existsByConsentId(dto.getConsentId())) {
            throw new RuntimeException("Already withdrawn");
        }

        ConsentRecord consent = consentRepository.findById(dto.getConsentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Consent not found with ID: " + dto.getConsentId())
                );

        ConsentWithdrawal w = ConsentWithdrawal.builder()
                .consentId(dto.getConsentId())
                .withdrawnBy(UserUtil.getCurrentUserId())
                .reason(dto.getReason())
                .effectOnData(dto.getEffectOnData())
                .withdrawnAt(LocalDateTime.now())
                .build();

        ConsentWithdrawal saved = withdrawalRepository.save(w);

        consent.setStatus(ConsentStatus.WITHDRAWN);
        consentRepository.save(consent);

        log.info("Consent withdrawn successfully");

        // Record provenance
        Map<String, Object> map = Map.of(
                "consentId", saved.getConsentId(),
                "withdrawnBy", saved.getWithdrawnBy()
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO(
                    "WITHDRAW_CONSENT",
                    "consent_withdrawal",
                    UserUtil.getCurrentUserId(),
                    saved.getWithdrawalId(),
                    mapper.writeValueAsString(map)
            );

            provenanceClient.recordProvenanceData(requestDTO);
        } catch (Exception e) {
            log.error("Error while recording provenance: {}", e.getMessage());
        }

        // Send notification
        try {
            notificationClient.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(UserUtil.getCurrentUserId())
                            .entityId(dto.getConsentId())
                            .message("Consent withdrawn for Participant by user ID: "
                                    + UserUtil.getCurrentUserId()
                                    + " | Reason: " + dto.getReason())
                            .category("CONSENT")
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send withdrawal notification: {}", e.getMessage());
        }

        return "Withdrawn successfully";
    }

    public String verifyConsent(Long id) throws Exception {

        log.info("Verifying consent ID: {}", id);

        ConsentRecord consent = consentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Consent not found with ID: " + id)
                );

        String storedHash = consent.getSignatureHash();
        String currentHash = HashUtil.generateSHA256(consent.getSignedDocumentUri());

        if (storedHash.equals(currentHash)) {
            return "File is NOT tampered (Valid)";
        } else {
            return "File has been TAMPERED!";
        }
    }

    public List<ConsentResponseDTO> getConsentsByStudyId(Long studyId) {

        log.info("Fetching consents for studyId: {}", studyId);

        return consentRepository.findByParticipantId_StudyId(studyId)
                .stream()
                .map(ConsentMapper::toResponse)
                .toList();
    }
}