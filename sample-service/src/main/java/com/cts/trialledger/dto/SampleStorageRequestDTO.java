package com.cts.trialledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleStorageRequestDTO {

    @NotNull(message = "Freezer ID is required")
    private Long freezerId;

    @NotBlank(message = "Shelf is required")
    private String shelf;

    @NotBlank(message = "Box is required")
    private String box;

    @NotBlank(message = "Position is required")
    private String position;
}