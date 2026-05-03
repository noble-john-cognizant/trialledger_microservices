package com.cts.adverseevent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AEFollowUpRequestDto {

    @NotBlank(message = "Action taken is required")
    private String actionTaken;

    @NotNull(message = "Performed By (user id) is required")
    private Long performedById;

    @NotNull(message = "Performed date is required")
    private LocalDateTime performedAt;

    private String notes;
}
