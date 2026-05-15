package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.AEFollowUpRequestDto;
import com.cts.trialledger.dto.AEFollowUpResponseDto;
import com.cts.trialledger.entity.AEFollowUp;
import com.cts.trialledger.entity.AdverseEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AEFollowUpMapper {

    public AEFollowUp toEntity(AEFollowUpRequestDto dto, AdverseEvent ae) {
        AEFollowUp followUp = new AEFollowUp();
        followUp.setAdverseEvent(ae);
        followUp.setActionTaken(dto.getActionTaken());
        followUp.setPerformedById(dto.getPerformedById());
        followUp.setPerformedAt(LocalDateTime.now());
        followUp.setNotes(dto.getNotes());
        followUp.setIsDeleted(false);
        return followUp;
    }

    public AEFollowUpResponseDto toResponse(AEFollowUp followUp) {
        AEFollowUpResponseDto dto = new AEFollowUpResponseDto();
        dto.setFollowUpId(followUp.getId());
        dto.setAeId(followUp.getAdverseEvent().getId());
        dto.setActionTaken(followUp.getActionTaken());
        dto.setPerformedById(followUp.getPerformedById());
        dto.setPerformedAt(followUp.getPerformedAt());
        dto.setNotes(followUp.getNotes());
        dto.setIsDeleted(followUp.getIsDeleted());
        return dto;
    }
}