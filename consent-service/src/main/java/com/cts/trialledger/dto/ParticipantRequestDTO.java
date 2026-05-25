package com.cts.trialledger.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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
    @NotBlank
    private String email;

    private String name;
    private LocalDate dob;
    @NotBlank
    @Length(min = 10, max = 10)
    private String phone;

}