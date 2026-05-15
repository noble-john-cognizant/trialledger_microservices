package com.cts.trialledger.service;

import com.cts.trialledger.client.ParticipantClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.StudyClient;
import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.AEFollowUp;
import com.cts.trialledger.entity.AdverseEvent;
import com.cts.trialledger.exception.AdverseEventNotFoundException;
import com.cts.trialledger.exception.ParticipantNotFoundException;
import com.cts.trialledger.exception.StudyNotFoundException;
import com.cts.trialledger.mapper.AEFollowUpMapper;
import com.cts.trialledger.mapper.AdverseEventMapper;
import com.cts.trialledger.model.AEStatus;
import com.cts.trialledger.model.Severity;
import com.cts.trialledger.repository.AEFollowUpRepository;
import com.cts.trialledger.repository.AdverseEventRepository;
import com.cts.trialledger.util.UserUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdverseEventService {

    private final AdverseEventRepository adverseEventRepository;
    private final AEFollowUpRepository aeFollowUpRepository;
    private final AdverseEventMapper aeMapper;
    private final AEFollowUpMapper followUpMapper;
    private final ProvenanceClient provenanceClient;
    private final StudyClient studyClient;
    private final ParticipantClient participantClient;


    public List<AdverseEventResponseDto> getAllAE() {
        return adverseEventRepository.findByIsDeletedFalse()
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdverseEventResponseDto getAEById(Long aeId) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));
        return aeMapper.toResponse(ae);
    }

    public List<AdverseEventResponseDto> getAEByStudy(Long studyId) {
        return adverseEventRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AdverseEventResponseDto> getAEByParticipant(Long participantId) {
        return adverseEventRepository.findByParticipantIdAndIsDeletedFalse(participantId)
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdverseEventResponseDto createAE(AdverseEventRequestDto dto) throws JsonProcessingException {

        // Validate study exists via Feign
        try {
            studyClient.getStudyById(dto.getStudyId());
        } catch (Exception ex) {
            throw new StudyNotFoundException(dto.getStudyId());
        }

        // Validate participant exists via Feign
        try {
            participantClient.getParticipantById(dto.getParticipantId());
        } catch (Exception ex) {
            throw new ParticipantNotFoundException(dto.getParticipantId());
        }

        AdverseEvent ae = aeMapper.toEntity(dto);
        AdverseEvent saved = adverseEventRepository.save(ae);
        AdverseEventResponseDto response = aeMapper.toResponse(saved);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("aeId", saved.getId());
        map.put("studyId", saved.getStudyId());
        map.put("severity", saved.getSeverity().name());
        map.put("reportedBy", saved.getReportedById());
        map.put("status", saved.getStatus().name());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_AE", "adverse_event", UserUtil.getCurrentUserId(), saved.getId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);
        return response;
    }

    public AdverseEventResponseDto updateStatus(Long aeId, String status) throws JsonProcessingException {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));
        ae.setStatus(AEStatus.valueOf(status.toUpperCase()));
        AdverseEvent saved = adverseEventRepository.save(ae);
        AdverseEventResponseDto response = aeMapper.toResponse(saved);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("aeId", ae.getId());
        map.put("status", ae.getStatus());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("UPDATE_AE_STATUS", "adverse_event", UserUtil.getCurrentUserId(), saved.getId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);

        return response;
    }

    public AdverseEventResponseDto updateSeverity(Long aeId, Severity severity) throws JsonProcessingException {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));
        ae.setSeverity(severity);
        AdverseEvent saved = adverseEventRepository.save(ae);
        AdverseEventResponseDto response = aeMapper.toResponse(saved);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("aeId", ae.getId());
        map.put("severity", ae.getSeverity());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("UPDATE_AE_SEVERITY", "adverse_event", UserUtil.getCurrentUserId(), saved.getId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);

        return response;
    }

    @Transactional
    public String deleteAE(Long aeId) throws JsonProcessingException {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        List<AEFollowUp> followUps =
                aeFollowUpRepository.findByAdverseEvent_IdAndIsDeletedFalse(aeId);
        followUps.forEach(f -> f.setIsDeleted(true));
        aeFollowUpRepository.saveAll(followUps);

        ae.setIsDeleted(true);
        adverseEventRepository.save(ae);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("aeId", ae.getId());
        map.put("status", ae.getStatus());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("DELETE_AE", "adverse_event", UserUtil.getCurrentUserId(), ae.getId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);

        return "Adverse Event with ID " + aeId + " deleted along with "
                + followUps.size() + " follow-up(s)";
    }


    public ApiResponseDto getFullAdverseEvent(Long aeId) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        AdverseEventResponseDto aeDto = aeMapper.toResponse(ae);

        List<AEFollowUpResponseDto> followUps =
                aeFollowUpRepository.findByAdverseEvent_IdAndIsDeletedFalse(aeId)
                        .stream()
                        .map(followUpMapper::toResponse)
                        .collect(Collectors.toList());

        StudyDto study = studyClient.getStudyById(ae.getStudyId());
        ParticipantDto participant = participantClient.getParticipantById(ae.getParticipantId());

        return new ApiResponseDto(aeDto, followUps, study, participant);
    }
}