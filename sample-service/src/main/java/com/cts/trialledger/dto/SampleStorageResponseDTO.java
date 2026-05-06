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
public class SampleStorageResponseDTO {

    private Long storageId;
    private Long sampleId;
    private Long freezerId;
    private String shelf;
    private String box;
    private String position;
    private LocalDateTime storedAt;
    private LocalDateTime retrievedAt;
}