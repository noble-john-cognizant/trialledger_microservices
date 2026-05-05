package com.cts.adverseevent.service;

import com.cts.adverseevent.client.ParticipantClient;
import com.cts.adverseevent.client.ProvenanceClient;
import com.cts.adverseevent.client.StudyClient;
import com.cts.adverseevent.dto.*;
import com.cts.adverseevent.entity.AEFollowUp;
import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.exception.AdverseEventNotFoundException;
import com.cts.adverseevent.mapper.AEFollowUpMapper;
import com.cts.adverseevent.mapper.AdverseEventMapper;
import com.cts.adverseevent.model.AEStatus;
import com.cts.adverseevent.repository.AEFollowUpRepository;
import com.cts.adverseevent.repository.AdverseEventRepository;
import com.cts.adverseevent.util.UserUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("UPDATE_AE_STATUS", "adverse_event", UserUtil.getCurrentUserId(), saved.getId(), metadata);
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
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("DELETE_AE", "adverse_event", UserUtil.getCurrentUserId(), ae.getId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);

        return "Adverse Event with ID " + aeId + " soft-deleted along with "
                + followUps.size() + " follow-up(s)";
    }

    /**
     * Aggregation endpoint: returns AE + follow-ups + study (Feign) + participant (Feign).
     */
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
