package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.StudyClient;
import com.cts.trialledger.dto.ParticipantRequestDTO;
import com.cts.trialledger.dto.ParticipantResponseDTO;
import com.cts.trialledger.dto.ProvenanceRequestDTO;
import com.cts.trialledger.entity.Participant;
import com.cts.trialledger.exception.DuplicateContactInfoException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.ParticipantMapper;
import com.cts.trialledger.model.EnrollmentStatus;
import com.cts.trialledger.repository.ParticipantRepository;
import com.cts.trialledger.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;

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

    public ParticipantResponseDTO createParticipant(ParticipantRequestDTO dto) {

        log.info("Creating participant with studyId: {}", dto.getStudyId());

        //  HANDLE STUDY SERVICE (Feign Exception)
        try {
            studyClient.getStudyById(dto.getStudyId());
        } catch (FeignException.NotFound ex) {

            log.error("Study not found: {}", dto.getStudyId());

            throw new ResourceNotFoundException("Study not found with ID: " + dto.getStudyId());

        } catch (FeignException.ServiceUnavailable ex) {

            log.error("Study Service is unavailable");

            throw new RuntimeException("Study Service is currently unavailable");
        }

        if (repo.existsByContactInfo(dto.getContactInfo())) {
            throw new DuplicateContactInfoException("Contact info already exists");
        }

        Participant p = ParticipantMapper.toEntity(dto);
        p.setEnrollmentStatus(EnrollmentStatus.PENDING);
        Participant saved;

        try {
            saved = repo.save(p);
        } catch (DataIntegrityViolationException ex) {

            String message = ex.getMostSpecificCause().getMessage().toLowerCase();

            if (message.contains("contact") || message.contains("unique")) {
                throw new DuplicateContactInfoException("Contact info already exists");
            }

            throw ex; // fallback
        }

        log.info("Participant created successfully with ID: {}", saved.getParticipantId());

        //Record
        Map<String, Object> map = Map.of("studyId", saved.getStudyId(), "enrollmentStatus", saved.getEnrollmentStatus(), "externalId", saved.getExternalId());

        try {
            ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_PARTICIPANT", "participant", UserUtil.getCurrentUserId(), saved.getParticipantId(), new ObjectMapper().writeValueAsString(map));
            provenanceClient.recordProvenanceData(requestDTO);
        } catch (Exception e) {
            log.error("Error while sending provenance data: {}", e.getMessage());
        }

        return ParticipantMapper.toResponse(saved);
    }

    //  GET ALL
    public List<ParticipantResponseDTO> getParticipants() {

        log.info("Fetching all participants");

        return repo.findAll().stream().map(ParticipantMapper::toResponse).collect(Collectors.toList());
    }

    //  GET BY ID
    public ParticipantResponseDTO getParticipantById(Long id) {

        log.info("Fetching participant with ID: {}", id);

        Participant p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        return ParticipantMapper.toResponse(p);
    }

    //  GET BY STUDY ID
    public List<Participant> getByStudyId(Long studyId) {

        log.info("Fetching participants for studyId: {}", studyId);

        return repo.findByStudyId(studyId);
    }

    public ParticipantResponseDTO studyFallback(ParticipantRequestDTO dto, Exception ex) {
        log.error("Study service is DOWN. Error: {}", ex.getMessage());
        throw new RuntimeException("Study Service is currently unavailable");
    }

    //Update enrollment Status
    public ParticipantResponseDTO updateEnrollmentStatus(Long participantId, EnrollmentStatus status) {

        Participant participant = repo.findById(participantId).orElseThrow(() -> new ResourceNotFoundException("participant not found"));


//  BLOCK manual ENROLLED
        if (status == EnrollmentStatus.ENROLLED) {
            throw new RuntimeException("ENROLLED status cannot be set manually");
        }


// COMPLETED only from ENROLLED
        if (status == EnrollmentStatus.COMPLETED) {
            if (participant.getEnrollmentStatus() != EnrollmentStatus.ENROLLED) {
                throw new RuntimeException("Only ENROLLED participants can be marked COMPLETED");
            }
        }

//  BLOCK update if already withdrawn
        if (participant.getEnrollmentStatus() == EnrollmentStatus.WITHDRAWN) {
            throw new RuntimeException("Cannot update withdrawn participant");
        }


        participant.setEnrollmentStatus(status);
        Participant updatedEnrollmentStatus = repo.save(participant);
        //Record
        Map<String, Object> map = Map.of("studyId", updatedEnrollmentStatus.getStudyId(),
                "enrollmentStatus", updatedEnrollmentStatus.getEnrollmentStatus(),
                "externalId", updatedEnrollmentStatus.getExternalId());

        try {
            ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("UPDATE_PARTICIPANT", "participant", UserUtil.getCurrentUserId(), updatedEnrollmentStatus.getParticipantId(), new ObjectMapper().writeValueAsString(map));
            provenanceClient.recordProvenanceData(requestDTO);
        } catch (Exception e) {
            log.error("Error while sending provenance data: {}", e.getMessage());
        }

        return ParticipantMapper.toResponse(updatedEnrollmentStatus);
    }

    public ParticipantResponseDTO cancelParticipant(Long participantId) {

        Participant participant = repo.findById(participantId).orElseThrow(() -> new RuntimeException("Participant not found with id: " + participantId));

        // Prevent re-cancel (optional but recommended)
        if (participant.getEnrollmentStatus() == EnrollmentStatus.WITHDRAWN) {
            throw new RuntimeException("Participant is already withdrawn");
        }

        // Soft delete → mark as WITHDRAWN
        participant.setEnrollmentStatus(EnrollmentStatus.WITHDRAWN);
        Participant saved = repo.save(participant);

        //Record
        Map<String, Object> map = Map.of("studyId", saved.getStudyId(),
                "enrollmentStatus", saved.getEnrollmentStatus(),
                "externalId", saved.getExternalId());

        try {
            ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CANCEL_PARTICIPANT", "participant", UserUtil.getCurrentUserId(), saved.getParticipantId(), new ObjectMapper().writeValueAsString(map));
            provenanceClient.recordProvenanceData(requestDTO);
        } catch (Exception e) {
            log.error("Error while sending provenance data: {}", e.getMessage());
        }

        return ParticipantMapper.toResponse(participant);
    }
}