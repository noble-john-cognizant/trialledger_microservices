package com.cts.trialledger.service;

import com.cts.trialledger.client.ProtocolClient;

import com.cts.trialledger.dto.*;

import com.cts.trialledger.entity.*;

import com.cts.trialledger.mapper.ConsentMapper;

import com.cts.trialledger.model.ConsentStatus;

import com.cts.trialledger.repository.*;

import com.cts.trialledger.util.HashUtil;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

import java.util.stream.Collectors;

@Slf4j

@Service

@RequiredArgsConstructor

public class ConsentService {

    private final ConsentRepository consentRepository;

    private final ParticipantRepository participantRepository;

    private final ConsentWithdrawalRepository withdrawalRepository;

    private final ProtocolClient protocolClient;

    //  CREATE CONSENT WITH FEIGN + CIRCUIT BREAKER

    @CircuitBreaker(name = "protocolService", fallbackMethod = "protocolFallback")

    public ConsentResponseDTO createConsent(ConsentRequestDTO dto) throws Exception {

        log.info("Creating consent for participantId: {}", dto.getParticipantId());

        // VALIDATE PARTICIPANT

        Participant p = participantRepository.findById(dto.getParticipantId())

                .orElseThrow(() -> new RuntimeException("Participant not found"));

        //  FEIGN CALL → VALIDATE PROTOCOL

        protocolClient.getProtocolVersion(dto.getProtocolId(),
                dto.getVersionNumber());

        ConsentRecord c = ConsentMapper.toEntity(dto, p);

        // HASH GENERATION

        c.setSignatureHash(HashUtil.generateSHA256(dto.getSignedDocumentUri()));

        c.setStatus(ConsentStatus.ACTIVE);

        ConsentRecord saved = consentRepository.save(c);

        log.info("Consent created successfully with ID: {}", saved.getConsentId());

        return ConsentMapper.toResponse(saved);

    }

    //  FALLBACK METHOD

    public ConsentResponseDTO protocolFallback(ConsentRequestDTO dto, Exception ex) {

        log.error("Protocol service is DOWN. Error: {}", ex.getMessage());

        throw new RuntimeException("Protocol Service is currently unavailable");

    }

    // GET BY PARTICIPANT

    public List<ConsentResponseDTO> getConsentsByParticipant(Long id) {

        log.info("Fetching consents for participantId: {}", id);

        return consentRepository.findByParticipantId_ParticipantId(id)

                .stream()

                .map(ConsentMapper::toResponse)

                .collect(Collectors.toList());

    }

    // WITHDRAW CONSENT

    public String withdrawConsent(ConsentWithdrawalDTO dto) {

        log.info("Withdrawing consent ID: {}", dto.getConsentId());

        if (withdrawalRepository.existsByConsentId(dto.getConsentId())) {

            throw new RuntimeException("Already withdrawn");

        }

        ConsentRecord consent = consentRepository.findById(dto.getConsentId())

                .orElseThrow(() -> new RuntimeException("Consent not found"));

        ConsentWithdrawal w = ConsentWithdrawal.builder()

                .consentId(dto.getConsentId())

                .withdrawnBy(dto.getWithdrawnBy())

                .reason(dto.getReason())

                .effectOnData(dto.getEffectOnData())

                .withdrawnAt(LocalDateTime.now())

                .build();

        withdrawalRepository.save(w);

        consent.setStatus(ConsentStatus.WITHDRAWN);

        consentRepository.save(consent);

        log.info("Consent withdrawn successfully");

        return "Withdrawn successfully";

    }

    // VERIFY CONSENT

    public String verifyConsent(Long id) throws Exception {

        log.info("Verifying consent ID: {}", id);

        ConsentRecord consent = consentRepository.findById(id)

                .orElseThrow(() -> new RuntimeException("Consent not found"));

        String storedHash = consent.getSignatureHash();

        String currentHash = HashUtil.generateSHA256(consent.getSignedDocumentUri());

        if (storedHash.equals(currentHash)) {

            return "File is NOT tampered (Valid)";

        } else {

            return "File has been TAMPERED!";

        }

    }

    // GET BY STUDY

    public List<ConsentResponseDTO> getConsentsByStudyId(Long studyId) {

        log.info("Fetching consents for studyId: {}", studyId);

        return consentRepository.findByParticipantId_StudyId(studyId)

                .stream()

                .map(ConsentMapper::toResponse)

                .toList();

    }

}
