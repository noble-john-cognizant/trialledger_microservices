package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.StudyClient;

import com.cts.trialledger.dto.ParticipantRequestDTO;

import com.cts.trialledger.dto.ParticipantResponseDTO;

import com.cts.trialledger.dto.ProvenanceRequestDTO;
import com.cts.trialledger.entity.Participant;

import com.cts.trialledger.mapper.ParticipantMapper;

import com.cts.trialledger.repository.ParticipantRepository;


import com.cts.trialledger.util.UserUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j

@Service

@RequiredArgsConstructor

public class ParticipantService {

    private final StudyClient studyClient;
    private final ProvenanceClient provenanceClient;
    private final ParticipantRepository repo;

    //  CREATE PARTICIPANT WITH FEIGN + CIRCUIT BREAKER

//    @CircuitBreaker(name = "studyService", fallbackMethod = "studyFallback")

    public ParticipantResponseDTO createParticipant(ParticipantRequestDTO dto) {

        log.info("Creating participant with studyId: {}", dto.getStudyId());

        //  FEIGN CALL → VALIDATE STUDY

        studyClient.getStudyById(dto.getStudyId());

        Participant p = ParticipantMapper.toEntity(dto);

        Participant saved = repo.save(p);

        log.info("Participant created successfully with ID: {}", saved.getParticipantId());

        //Record
        Map<String, Object> map = Map.of("studyId", saved.getStudyId(),
                "enrollmentStatus", saved.getEnrollmentStatus(),
                "externalId", saved.getExternalId()
        );
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_PARTICIPANT", "participant", UserUtil.getCurrentUserId(), saved.getParticipantId(), new ObjectMapper().writeValueAsString(map));
        provenanceClient.recordProvenanceData(requestDTO);
        return ParticipantMapper.toResponse(saved);

    }

    //  FALLBACK METHOD

    public ParticipantResponseDTO studyFallback(ParticipantRequestDTO dto, Exception ex) {

        log.error("Study service is DOWN. Error: {}", ex.getMessage());

        throw new RuntimeException("Study Service is currently unavailable");

    }

    // GET ALL

    public List<ParticipantResponseDTO> getParticipants() {

        log.info("Fetching all participants");

        return repo.findAll()

                .stream()

                .map(ParticipantMapper::toResponse)

                .collect(Collectors.toList());

    }

    // GET BY ID

    public ParticipantResponseDTO getParticipantById(Long id) {

        log.info("Fetching participant with ID: {}", id);

        Participant p = repo.findById(id)

                .orElseThrow(() -> new RuntimeException("Participant not found"));

        return ParticipantMapper.toResponse(p);

    }

    // GET BY STUDY ID

    public List<Participant> getByStudyId(Long studyId) {

        log.info("Fetching participants for studyId: {}", studyId);

        return repo.findByStudyId(studyId);

    }

}