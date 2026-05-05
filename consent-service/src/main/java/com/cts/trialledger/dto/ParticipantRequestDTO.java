package com.cts.trialledger.dto;

import com.cts.trialledger.model.EnrollmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantRequestDTO {

    @NotNull
    private Long studyId;

    @NotBlank
    private String externalId;

    private String name;
    private LocalDate dob;
    private String contactInfo;

    @NotNull
    private EnrollmentStatus enrollmentStatus;
}
