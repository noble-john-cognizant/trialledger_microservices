package com.cts.visit.service;

import com.cts.visit.client.NotificationClient;
import com.cts.visit.client.ParticipantClient;
import com.cts.visit.client.ProvenanceClient;
import com.cts.visit.client.StudyClient;
import com.cts.visit.dto.*;
import com.cts.visit.entity.Visit;
import com.cts.visit.enums.VisitStatus;
import com.cts.visit.exception.ResourceNotFoundException;
import com.cts.visit.mapper.VisitMapper;
import com.cts.visit.repository.VisitRepository;
import com.cts.visit.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final StudyClient studyClient;
    private final ParticipantClient participantClient;
    private final ProvenanceClient provenanceClient;
    private final NotificationClient  notificationClient;

    public VisitService(VisitRepository visitRepository,
                        StudyClient studyClient,
                        ParticipantClient participantClient, ProvenanceClient provenanceClient, NotificationClient notificationClient) {
        this.visitRepository = visitRepository;
        this.studyClient = studyClient;
        this.participantClient = participantClient;
        this.provenanceClient = provenanceClient;
        this.notificationClient = notificationClient;
    }

    // 1. Schedule a new visit
    public VisitResponseDto scheduleVisit(VisitRequestDto request) throws JsonProcessingException {

        // Validate participant exists (via participant-service)
        validateParticipantExists(request.getParticipantId());

        // Validate study exists (via study-service)
        validateStudyExists(request.getStudyId());

        Visit visit = VisitMapper.toEntity(request);
        visit.setStatus(VisitStatus.SCHEDULED); // default status

        Visit savedVisit = visitRepository.save(visit);

        try {
            notificationClient.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(UserUtil.getCurrentUserId())
                            .entityId(savedVisit.getVisitId())
                            .message("Visit scheduled for Participant ID: " + savedVisit.getParticipantId()
                                    + " | Type: " + savedVisit.getVisitType()
                                    + " | Scheduled at: " + savedVisit.getScheduledAt())
                            .category("VISIT")
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send visit scheduled notification: {}", e.getMessage());
        }

        //Record
        Map<String, Object> map = Map.of("participantId", visit.getParticipantId(),
                "visitType", savedVisit.getVisitType(),
                "StudyId", visit.getStudyId(),
                "status", savedVisit.getStatus()
        );
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO("SCHEDULE_VISIT", "visit", UserUtil.getCurrentUserId(), savedVisit.getVisitId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(dto);
        return VisitMapper.toResponseDto(savedVisit);
    }

    // 2. Get all visits for a participant
    public List<VisitResponseDto> getVisitsByParticipant(Long participantId) {

        // Validate participant exists in participant-service
        validateParticipantExists(participantId);

        return visitRepository.findByParticipantId(participantId)
                .stream()
                .map(VisitMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // 3. Update visit status (Completed / Missed / Cancelled / Scheduled)
    public VisitResponseDto updateVisitStatus(Long visitId, VisitStatus status, LocalDateTime performedAt)
            throws JsonProcessingException {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        visit.setStatus(status);

        // Only COMPLETED carries a performedAt timestamp.
        //   - Use the supplied value if provided
        //   - Otherwise default to "now"
        //   - For every other status, clear any previously stored performedAt
        //     so the field accurately reflects the current state.
        if (status == VisitStatus.COMPLETED) {
            visit.setPerformedAt(performedAt != null ? performedAt : LocalDateTime.now());
        } else {
            visit.setPerformedAt(null);
        }

        Visit updatedVisit = visitRepository.save(visit);

        if (status == VisitStatus.MISSED) {
            try {
                notificationClient.createNotification(
                        NotificationRequestDTO.builder()
                                .userId(UserUtil.getCurrentUserId())
                                .entityId(visitId)
                                .message("ALERT: Visit ID " + visitId
                                        + " has been marked as MISSED for Participant ID: "
                                        + visit.getParticipantId())
                                .category("VISIT")
                                .build()
                );
            } catch (Exception e) {
                log.warn("Failed to send visit missed notification: {}", e.getMessage());
            }
        }

        //Record
        Map<String, Object> map = Map.of("participantId", updatedVisit.getParticipantId(),
                "visitType", updatedVisit.getVisitType(),
                "StudyId", updatedVisit.getStudyId(),
                "status", updatedVisit.getStatus()
        );
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO("UPDATE_VISIT_STATUS", "visit", UserUtil.getCurrentUserId(), updatedVisit.getVisitId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(dto);
        return VisitMapper.toResponseDto(updatedVisit);
    }

    // 4. Get visit by ID
    public VisitResponseDto getVisitById(Long visitId) throws JsonProcessingException {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));
//Record
        Map<String, Object> map = Map.of("participantId", visit.getParticipantId(),
                "visitType", visit.getVisitType(),
                "StudyId", visit.getStudyId(),
                "status", visit.getStatus()
        );
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO("UPDATE_VISIT", "visit", UserUtil.getCurrentUserId(), visit.getVisitId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(dto);
        return VisitMapper.toResponseDto(visit);
    }

    // 5. Cancel (Soft Delete) Visit
    public VisitResponseDto cancelVisit(Long visitId) throws JsonProcessingException {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        // Business rule: Completed visit cannot be cancelled
        if (VisitStatus.COMPLETED.equals(visit.getStatus())) {
            throw new IllegalStateException("Completed visit cannot be cancelled");
        }
        if(VisitStatus.CANCELLED.equals(visit.getStatus())) {
            throw new IllegalStateException("Visit is already cancelled");
        }

        visit.setStatus(VisitStatus.CANCELLED);
        Visit updatedVisit = visitRepository.save(visit);

        //Record
        Map<String, Object> map = Map.of("participantId", updatedVisit.getParticipantId(),
                "visitType", updatedVisit.getVisitType(),
                "StudyId", updatedVisit.getStudyId(),
                "status", updatedVisit.getStatus()
        );
        ObjectMapper mapper = new ObjectMapper();
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO("CANCEL_VISIT", "visit", UserUtil.getCurrentUserId(), updatedVisit.getVisitId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(dto);
        return VisitMapper.toResponseDto(updatedVisit);
    }

    // 6.Get all visits for a study
    public List<VisitResponseDto> getVisitsByStudy(Long studyId) {

        // Validate study exists (via study-service)
        validateStudyExists(studyId);

        return visitRepository.findByStudyId(studyId)
                .stream()
                .map(VisitMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // ==================== Helper methods (Feign validation) ====================

    private void validateParticipantExists(Long participantId) {
        try {
            ParticipantResponseDto response =
                    participantClient.getParticipantById(participantId);

            if (response == null || response.getParticipantId() == null) {
                throw new ResourceNotFoundException(
                        "Participant not found with id: " + participantId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Participant not found with id: " + participantId);
        }
    }

    private void validateStudyExists(Long studyId) {
        try {
            StudyResponseDto response =
                    studyClient.getStudyById(studyId);

            if (response == null || response.getStudyId() == null) {
                throw new ResourceNotFoundException(
                        "Study not found with id: " + studyId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Study not found with id: " + studyId);
        }
    }
}