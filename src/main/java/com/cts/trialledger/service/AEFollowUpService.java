package com.cts.trialledger.service;

import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.dto.AEFollowUpRequestDto;
import com.cts.trialledger.dto.AEFollowUpResponseDto;
import com.cts.trialledger.dto.ProvenanceRequestDTO;
import com.cts.trialledger.entity.AEFollowUp;
import com.cts.trialledger.entity.AdverseEvent;
import com.cts.trialledger.exception.AEFollowUpNotFoundException;
import com.cts.trialledger.exception.AdverseEventNotFoundException;
import com.cts.trialledger.mapper.AEFollowUpMapper;
import com.cts.trialledger.repository.AEFollowUpRepository;
import com.cts.trialledger.repository.AdverseEventRepository;
import com.cts.trialledger.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AEFollowUpService {

    private final AEFollowUpRepository followUpRepository;
    private final AdverseEventRepository adverseEventRepository;
    private final AEFollowUpMapper followUpMapper;
    private final ProvenanceClient provenanceClient;

    public AEFollowUpService(AEFollowUpRepository followUpRepository,
                             AdverseEventRepository adverseEventRepository,
                             AEFollowUpMapper followUpMapper, ProvenanceClient provenanceClient) {
        this.followUpRepository = followUpRepository;
        this.adverseEventRepository = adverseEventRepository;
        this.followUpMapper = followUpMapper;
        this.provenanceClient = provenanceClient;
    }

    public AEFollowUpResponseDto addFollowUp(Long aeId, AEFollowUpRequestDto dto) throws JsonProcessingException {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        AEFollowUp followUp = followUpMapper.toEntity(dto, ae);
        AEFollowUp saved = followUpRepository.save(followUp);
        AEFollowUpResponseDto response = followUpMapper.toResponse(saved);

        // Record in provenance table
        Map<String, Object> map = Map.of("follow_up", saved.getId(), "adverse_event_id", aeId, "performedBy", saved.getPerformedById(), "is_deleted", followUp.getIsDeleted());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_AE_FOLLOW_UP", "ae_follow_up", UserUtil.getCurrentUserId(), saved.getId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(requestDTO);

        return response;
    }

    public List<AEFollowUpResponseDto> getFollowUps(Long aeId) {
        if (adverseEventRepository.findByIdAndIsDeletedFalse(aeId).isEmpty()) {
            throw new AdverseEventNotFoundException(aeId);
        }
        return followUpRepository.findByAdverseEvent_IdAndIsDeletedFalse(aeId)
                .stream()
                .map(followUpMapper::toResponse)
                .collect(Collectors.toList());
    }

    public String deleteFollowUp(Long followUpId) throws JsonProcessingException {
        AEFollowUp followUp = followUpRepository.findByIdAndIsDeletedFalse(followUpId)
                .orElseThrow(() -> new AEFollowUpNotFoundException(followUpId));
        followUp.setIsDeleted(true);
        followUpRepository.save(followUp);

        // Record in provenance table
        Map<String, Object> map = Map.of("follow_up", followUpId, "is_deleted", followUp.getIsDeleted());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("DELETE_AE_FOLLOW_UP", "ae_follow_up", UserUtil.getCurrentUserId(), followUp.getId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(requestDTO);

        return "Follow-up with ID " + followUpId + " deleted";
    }
}