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

    @NotNull(message = "Approved By (user id) is required")
    private Long approvedById;
}
