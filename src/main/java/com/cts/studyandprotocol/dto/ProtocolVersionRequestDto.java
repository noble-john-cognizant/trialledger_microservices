package com.cts.studyandprotocol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ProtocolVersionRequestDto {

    @NotBlank(message = "Version number is required")
    private String versionNumber;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    @NotNull(message = "Effective date is required")
    private LocalDate effectiveDate;

<<<<<<< HEAD
=======
    @NotNull(message = "Approved By (user id) is required")
    private Long approvedById;
>>>>>>> 09b0807c705d15c7b53b6bb00bf12bc6fa4e2ec9
}
