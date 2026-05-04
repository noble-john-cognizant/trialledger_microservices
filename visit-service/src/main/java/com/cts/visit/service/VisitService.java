package com.cts.visit.service;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.client.ParticipantClient;
import com.cts.visit.client.StudyClient;
import com.cts.visit.dto.ParticipantResponseDto;
import com.cts.visit.dto.StudyResponseDto;
import com.cts.visit.dto.VisitRequestDto;
import com.cts.visit.dto.VisitResponseDto;
import com.cts.visit.entity.Visit;
import com.cts.visit.enums.VisitStatus;
import com.cts.visit.exception.ResourceNotFoundException;
import com.cts.visit.mapper.VisitMapper;
import com.cts.visit.repository.VisitRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final StudyClient studyClient;
    private final ParticipantClient participantClient;

    public VisitService(VisitRepository visitRepository,
                        StudyClient studyClient,
                        ParticipantClient participantClient) {
        this.visitRepository = visitRepository;
        this.studyClient = studyClient;
        this.participantClient = participantClient;
    }

    // 1. Schedule a new visit
    public VisitResponseDto scheduleVisit(VisitRequestDto request) {

        // Validate participant exists (via participant-service)
        validateParticipantExists(request.getParticipantId());

        // Validate study exists (via study-service)
        validateStudyExists(request.getStudyId());

        Visit visit = VisitMapper.toEntity(request);
        visit.setStatus(VisitStatus.SCHEDULED); // default status

        Visit savedVisit = visitRepository.save(visit);
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
    public VisitResponseDto updateVisitStatus(Long visitId, VisitStatus status) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        visit.setStatus(status);
        Visit updatedVisit = visitRepository.save(visit);
        return VisitMapper.toResponseDto(updatedVisit);
    }

    // 4. Get visit by ID
    public VisitResponseDto getVisitById(Long visitId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        return VisitMapper.toResponseDto(visit);
    }

    // 5. Cancel (Soft Delete) Visit
    public VisitResponseDto cancelVisit(Long visitId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Visit not found"));

        // Business rule: Completed visit cannot be cancelled
        if (VisitStatus.COMPLETED.equals(visit.getStatus())) {
            throw new IllegalStateException("Completed visit cannot be cancelled");
        }

        visit.setStatus(VisitStatus.CANCELLED);
        Visit updatedVisit = visitRepository.save(visit);
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
            ApiResponseDto<ParticipantResponseDto> response =
                    participantClient.getParticipantById(participantId);

            if (response == null
                    || response.getData() == null
                    || response.getData().getParticipantId() == null) {
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
            ApiResponseDto<StudyResponseDto> response =
                    studyClient.getStudyById(studyId);

            if (response == null
                    || response.getData() == null
                    || response.getData().getStudyId() == null) {
                throw new ResourceNotFoundException(
                        "Study not found with id: " + studyId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(
                    "Study not found with id: " + studyId);
        }
    }
}
