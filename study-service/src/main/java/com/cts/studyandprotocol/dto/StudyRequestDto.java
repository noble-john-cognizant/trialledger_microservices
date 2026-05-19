package com.cts.studyandprotocol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Sponsor is required")
    private String sponsor;

    @NotBlank(message = "Protocol Number is required")
    private String protocolNumber;

    @NotNull(message = "Start Date is required")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    private LocalDate endDate;
}
