package com.cts.trialledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssayRunRequestDTO {

    @NotNull(message = "Sample ID is required")
    private Long sampleId;

    @NotNull(message = "Instrument ID is required")
    private Long instrumentId;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotBlank(message = "Protocol ref is required")
    private String protocolRef;

//    @NotBlank(message = "Result URI is required")
//    private String resultUri;

    private String metadataJson;

}