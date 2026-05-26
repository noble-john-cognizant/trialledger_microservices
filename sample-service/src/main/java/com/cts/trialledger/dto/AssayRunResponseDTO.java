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
public class AssayRunResponseDTO {
    private Long assayId;
    private Long sampleId;
    private Long instrumentId;
    private Long operatorId;
    private LocalDateTime runDate;
    private String protocolRef;
    private String resultUri;
    private String metadataJson;
}