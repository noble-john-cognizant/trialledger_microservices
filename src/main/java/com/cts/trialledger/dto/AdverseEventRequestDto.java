package com.cts.trialledger.dto;

import com.cts.trialledger.model.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdverseEventRequestDto {

    @NotNull(message = "Participant Id is required")
    private Long participantId;

    @NotNull(message = "Study Id is required")
    private Long studyId;

    @NotNull(message = "Severity is required")
    private Severity severity;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Reported By (user id) is required")
    private Long reportedById;
}