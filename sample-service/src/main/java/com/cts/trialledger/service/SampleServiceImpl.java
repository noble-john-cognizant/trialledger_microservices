package com.cts.trialledger.service;

import com.cts.trialledger.client.ParticipantClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.StudyClient;
import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.ChainOfCustody;
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
//import com.cts.trialledger.service.AuditService;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import com.cts.trialledger.repository.SampleStorageRepository;
import com.cts.trialledger.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SampleServiceImpl implements SampleService {

    private final ParticipantClient participantClient;
    private final StudyClient studyClient;
    private final SampleRepository sampleRepository;
    private final ProvenanceClient provenanceClient;
    private final SampleMapper sampleMapper;
    private SampleStatus status;
    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final ChainOfCustodyMapper chainOfCustodyMapper;
    private final SampleStorageMapper sampleStorageMapper;
    private final SampleStorageRepository sampleStorageRepository;
    private final AssayRunRepository assayRunRepository;
    private final AssayRunMapper assayRunMapper;

    @Override
    public SampleResponseDTO createSample(SampleRequestDTO requestDTO) {

        Sample sample = sampleMapper.toEntity(requestDTO);
        validateStudy(requestDTO.getStudyId());
        validateParticipant(requestDTO.getParticipantId(), requestDTO.getStudyId());
        Sample saved = sampleRepository.save(sample);


        Map<String, Object> map = Map.of(
                "studyId", saved.getStudyId(),
                "sampleType", saved.getSampleType(),
                "collectedBy", saved.getCollectedBy(),
                "status", saved.getStatus(),
                "initialLocation", saved.getInitialLocation()
        );
        ProvenanceRequestDTO request = new ProvenanceRequestDTO("CREATE_SAMPLE",
                "sample", UserUtil.getCurrentUserId(),
                saved.getSampleId(),
                new ObjectMapper().writeValueAsString(map));
        provenanceClient.recordProvenanceData(request);

        return sampleMapper.toResponseDTO(saved);
    }

    private void validateParticipant(Long participantId, Long studyId) {
        ParticipantResponseDTO participant = participantClient.getParticipantById(participantId);
        if (!participant.getStudyId().equals(studyId)) {
            throw new IllegalArgumentException(
                    "Participant id=" + participantId + " belongs to study id="
                            + participant.getStudyId() + ", not study id=" + studyId);
        }
    }

    private void validateStudy(Long studyId) {
        StudyResponseDTO study = studyClient.getStudyById(studyId);
        if (!"ACTIVE".equalsIgnoreCase(study.getStatus())) {
            throw new IllegalArgumentException(
                    "Study id=" + studyId + " is not ACTIVE (current status="
                            + study.getStatus() + ")");
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