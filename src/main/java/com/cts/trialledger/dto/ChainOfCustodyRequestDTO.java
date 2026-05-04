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
public class ChainOfCustodyRequestDTO {

    @NotNull(message = "Sample ID is required")
    private Long sampleId;

    @NotBlank(message = "From user is required")
    private String fromUser;

    @NotBlank(message = "To user is required")
    private String toUser;

    @NotNull(message = "Transfer time is required")
    private LocalDateTime transferAt;

    @NotBlank(message = "From location is required")
    private String fromLocation;

    @NotBlank(message = "To location is required")
    private String toLocation;

    private String notes;
}