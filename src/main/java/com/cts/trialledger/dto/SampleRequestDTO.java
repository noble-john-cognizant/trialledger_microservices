package com.cts.trialledger.dto;

import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.model.SampleType;
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
public class SampleRequestDTO {

    @NotNull(message = "Participant ID is required")
    private Long participantId;

    @NotNull(message = "Study ID is required")
    private Long studyId;

    @NotNull(message = "Sample type is required")
    private SampleType sampleType;

    @NotNull(message = "Collected at is required")
    private LocalDateTime collectedAt;

    @NotBlank(message = "Collected by is required")
    private String collectedBy;

    @NotBlank(message = "Initial location is required")
    private String initialLocation;

    @NotNull(message = "Status is required")
    private SampleStatus status;
}