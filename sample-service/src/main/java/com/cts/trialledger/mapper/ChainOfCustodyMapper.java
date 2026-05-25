package com.cts.trialledger.mapper;

import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;
import com.cts.trialledger.entity.ChainOfCustody;
import org.springframework.stereotype.Component;

@Component
public class ChainOfCustodyMapper {

    public ChainOfCustodyResponseDTO toResponseDTO(ChainOfCustody coc) {
        return ChainOfCustodyResponseDTO.builder()
                .cocId(coc.getCocId())
                .sampleId(coc.getSample().getSampleId())
                .fromUser(coc.getFromUser())
                .toUser(coc.getToUser())
                .transferAt(coc.getTransferAt())
                .fromLocation(coc.getFromLocation())
                .toLocation(coc.getToLocation())
                .notes(coc.getNotes())
                .build();
    }
}