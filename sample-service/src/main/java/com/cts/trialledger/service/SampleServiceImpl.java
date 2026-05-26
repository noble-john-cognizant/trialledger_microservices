package com.cts.trialledger.service;

import com.cts.trialledger.client.*;
import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.AssayRunMapper;
import com.cts.trialledger.mapper.ChainOfCustodyMapper;
import com.cts.trialledger.mapper.SampleMapper;
import com.cts.trialledger.mapper.SampleStorageMapper;
import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.repository.AssayRunRepository;
import com.cts.trialledger.repository.ChainOfCustodyRepository;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.repository.SampleStorageRepository;
import com.cts.trialledger.util.UserUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    private final ParticipantClient participantClient;
    private final StudyClient studyClient;
    private final SampleRepository sampleRepository;
    private final ProvenanceClient provenanceClient;
    private final SampleMapper sampleMapper;
    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final ChainOfCustodyMapper chainOfCustodyMapper;
    private final SampleStorageMapper sampleStorageMapper;
    private final SampleStorageRepository sampleStorageRepository;
    private final AssayRunRepository assayRunRepository;
    private final AssayRunMapper assayRunMapper;
    private final KPIClient kpiClient;
    private final NotificationClient notificationClient;

    @Override
    public SampleResponseDTO createSample(SampleRequestDTO requestDTO) {

        Sample sample = sampleMapper.toEntity(requestDTO);

        validateAll(requestDTO.getStudyId(), requestDTO.getParticipantId());
        Sample saved = sampleRepository.save(sample);

        try {
            kpiClient.refreshAllKPIsForStudy(saved.getStudyId());
        } catch (Exception e) {
            log.warn("KPI refresh failed after sample save: {}", e.getMessage());
        }

        try {
            Map<String, Object> map = Map.of(
                    "studyId", saved.getStudyId(),
                    "sampleType", saved.getSampleType(),
                    "collectedBy", saved.getCollectedBy(),
                    "status", saved.getStatus(),
                    "initialLocation", saved.getInitialLocation()
            );
            ProvenanceRequestDTO request = new ProvenanceRequestDTO(
                    "CREATE_SAMPLE", "sample",
                    UserUtil.getCurrentUserId(),
                    saved.getSampleId(),
                    new ObjectMapper().writeValueAsString(map)
            );
            provenanceClient.recordProvenanceData(request);
        } catch (Exception e) {
            log.warn("Failed to record sample provenance: {}", e.getMessage());
        }

        try {
            notificationClient.createNotification(
                    NotificationRequestDTO.builder()
                            .userId(UserUtil.getCurrentUserId())
                            .entityId(saved.getSampleId())
                            .message("New sample collected: ID " + saved.getSampleId()
                                    + " | Type: " + saved.getSampleType()
                                    + " | Participant ID: " + saved.getParticipantId()
                                    + " | Study ID: " + saved.getStudyId())
                            .category("SAMPLE")
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to send sample notification: {}", e.getMessage());
        }

        return sampleMapper.toResponseDTO(saved);
    }


    private void validateAll(Long studyId, Long participantId) {
        List<String> errors = new ArrayList<>();

        // --- Study validation ---
        StudyResponseDTO study = null;
        try {
            study = studyClient.getStudyById(studyId);
            if (study == null) {
                errors.add("Study not found with id=" + studyId);
            } else if (!"ACTIVE".equalsIgnoreCase(study.getStatus())) {
                errors.add("Study id=" + studyId + " is not ACTIVE (current status=" + study.getStatus() + ")");
            }
        } catch (FeignException.NotFound ex) {
            errors.add("Study not found with id=" + studyId);
        } catch (FeignException ex) {
            errors.add("Study service is unavailable. Please try again later.");
        }

        // --- Participant validation ---
        ParticipantResponseDTO participant = null;
        try {
            participant = participantClient.getParticipantById(participantId);
            if (participant == null) {
                errors.add("Participant not found with id=" + participantId);
            } else {
                // Only cross-check study if study itself was found
                if (study != null && !participant.getStudyId().equals(studyId)) {
                    errors.add("Participant id=" + participantId
                            + " belongs to study id=" + participant.getStudyId()
                            + ", not study id=" + studyId);
                }
                if (!"ENROLLED".equalsIgnoreCase(participant.getEnrollmentStatus())) {
                    errors.add("Participant id=" + participantId + " is not ENROLLED"
                            + " (current status=" + participant.getEnrollmentStatus() + ")."
                            + " Samples can only be logged for actively enrolled participants.");
                }
            }
        } catch (FeignException.NotFound ex) {
            errors.add("Participant not found with id=" + participantId);
        } catch (FeignException ex) {
            errors.add("Participant service is unavailable. Please try again later.");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }
    }

    @Override
    public List<SampleResponseDTO> getAllSamples() {
        return sampleRepository.findAll()
                .stream()
                .map(sampleMapper::toResponseDTO)
                .toList();
    }

    @Override
    public SampleResponseDTO getSampleById(Long sampleId) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));
        return sampleMapper.toResponseDTO(sample);
    }


    @Override
    public List<SampleResponseDTO> getSamplesByParticipant(Long participantId) {

        List<SampleResponseDTO> samples = sampleRepository
                .findByParticipantId(participantId)
                .stream()
                .map(sampleMapper::toResponseDTO)
                .toList();

        if (samples.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No samples found for participant ID: " + participantId
            );
        }


        return samples;
    }

    @Override
    public List<SampleResponseDTO> getSamplesByStatus(SampleStatus status) {

        List<SampleResponseDTO> samples = sampleRepository
                .findByStatus(status)
                .stream()
                .map(sampleMapper::toResponseDTO)
                .toList();

        if (samples.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No samples found with status: " + status
            );
        }
        return samples;
    }

    @Override
    public List<SampleResponseDTO> getSamplesByStudy(Long studyId) {
        List<SampleResponseDTO> samples = sampleRepository
                .findByStudyId(studyId)
                .stream()
                .map(sampleMapper::toResponseDTO)
                .toList();

        if (samples.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No samples found for study ID: " + studyId);
        }
        return samples;
    }

    @Override
    public SampleResponseDTO updateStatus(Long sampleId, SampleStatus status) {
        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        SampleStatus current = sample.getStatus();

        // Enforce lifecycle order: COLLECTED → IN_ANALYSIS → COMPLETED
        boolean valid = switch (status) {
            case IN_ANALYSIS -> current == SampleStatus.COLLECTED;
            case COMPLETED   -> current == SampleStatus.IN_ANALYSIS;
            default          -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Cannot transition sample id=" + sampleId
                            + " from " + current + " to " + status
                            + ". Valid transitions: COLLECTED → IN_ANALYSIS → COMPLETED.");
        }

        sample.setStatus(status);
        Sample saved = sampleRepository.save(sample);

        try {
            Map<String, Object> map = Map.of(
                    "sampleId", saved.getSampleId(),
                    "previousStatus", current,
                    "newStatus", saved.getStatus()
            );
            ProvenanceRequestDTO request = new ProvenanceRequestDTO(
                    "UPDATE_SAMPLE_STATUS", "sample",
                    UserUtil.getCurrentUserId(),
                    saved.getSampleId(),
                    new ObjectMapper().writeValueAsString(map)
            );
            provenanceClient.recordProvenanceData(request);
        } catch (Exception e) {
            log.warn("Failed to record status-update provenance: {}", e.getMessage());
        }

        return sampleMapper.toResponseDTO(saved);
    }

    @Override
    public ApiResponseDTO getSampleFull(Long sampleId) {

        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        List<ChainOfCustodyResponseDTO> custody = chainOfCustodyRepository
                .findBySample_SampleId(sampleId)
                .stream().map(chainOfCustodyMapper::toResponseDTO).toList();

        List<SampleStorageResponseDTO> storage = sampleStorageRepository
                .findBySample_SampleId(sampleId)
                .stream().map(sampleStorageMapper::toResponseDTO).toList();

        List<AssayRunResponseDTO> assays = assayRunRepository
                .findBySample_SampleId(sampleId)
                .stream().map(assayRunMapper::toResponseDTO).toList();


        StudyResponseDTO study = studyClient.getStudyById(sample.getStudyId());
        ParticipantResponseDTO participant = participantClient.getParticipantById(sample.getParticipantId());

        ApiResponseDTO response = new ApiResponseDTO();
        response.setSample(sampleMapper.toResponseDTO(sample));
        response.setChainOfCustody(custody);
        response.setStorageHistory(storage);
        response.setAssayRuns(assays);
        response.setStudy(study);
        response.setParticipant(participant);

        return response;
    }

}