package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChainOfCustodyResponseDTO {

    private Long cocId;
    private Long sampleId;
    private String fromUser;
    private String toUser;
    private LocalDateTime transferAt;
    private String fromLocation;
    private String toLocation;
    private String notes;
}