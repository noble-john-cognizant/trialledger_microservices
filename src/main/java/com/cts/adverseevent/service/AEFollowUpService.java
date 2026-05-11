package com.cts.adverseevent.service;

import com.cts.adverseevent.dto.AEFollowUpRequestDto;
import com.cts.adverseevent.dto.AEFollowUpResponseDto;
import com.cts.adverseevent.entity.AEFollowUp;
import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.exception.AEFollowUpNotFoundException;
import com.cts.adverseevent.exception.AdverseEventNotFoundException;
import com.cts.adverseevent.mapper.AEFollowUpMapper;
import com.cts.adverseevent.repository.AEFollowUpRepository;
import com.cts.adverseevent.repository.AdverseEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AEFollowUpService {

    private final AEFollowUpRepository followUpRepository;
    private final AdverseEventRepository adverseEventRepository;
    private final AEFollowUpMapper followUpMapper;

    public AEFollowUpService(AEFollowUpRepository followUpRepository,
                             AdverseEventRepository adverseEventRepository,
                             AEFollowUpMapper followUpMapper) {
        this.followUpRepository = followUpRepository;
        this.adverseEventRepository = adverseEventRepository;
        this.followUpMapper = followUpMapper;
    }

    public AEFollowUpResponseDto addFollowUp(Long aeId, AEFollowUpRequestDto dto) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        AEFollowUp followUp = followUpMapper.toEntity(dto, ae);
        AEFollowUp saved = followUpRepository.save(followUp);
        return followUpMapper.toResponse(saved);
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

    public String deleteFollowUp(Long followUpId) {
        AEFollowUp followUp = followUpRepository.findByIdAndIsDeletedFalse(followUpId)
                .orElseThrow(() -> new AEFollowUpNotFoundException(followUpId));
        followUp.setIsDeleted(true);
        followUpRepository.save(followUp);
        return "Follow-up with ID " + followUpId + " soft-deleted";
    }
}
