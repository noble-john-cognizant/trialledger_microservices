package com.cts.adverseevent.service;

import com.cts.adverseevent.client.ProvenanceClient;
import com.cts.adverseevent.dto.AEFollowUpRequestDto;
import com.cts.adverseevent.dto.AEFollowUpResponseDto;
import com.cts.adverseevent.dto.ProvenanceRequestDTO;
import com.cts.adverseevent.entity.AEFollowUp;
import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.exception.AEFollowUpNotFoundException;
import com.cts.adverseevent.exception.AdverseEventNotFoundException;
import com.cts.adverseevent.mapper.AEFollowUpMapper;
import com.cts.adverseevent.repository.AEFollowUpRepository;
import com.cts.adverseevent.repository.AdverseEventRepository;
import com.cts.adverseevent.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("DELETE_AE_FOLLOW_UP", "ae_follow_up", UserUtil.getCurrentUserId(), followUp.getId(), mapper.writeValueAsString(map));
        provenanceClient.recordProvenanceData(requestDTO);

        return "Follow-up with ID " + followUpId + " soft-deleted";
    }
}
