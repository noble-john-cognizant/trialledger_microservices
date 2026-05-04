package com.cts.trialledger.dto;

import com.cts.trialledger.model.ConsentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentRequestDTO {

    @NotNull
    private Long participantId;

    @NotNull
    private Long protocolId;

    @NotNull
    private String versionNumber;

    private LocalDateTime consentDate;

    @NotNull
    private ConsentMethod consentMethod;

    @NotBlank
    private String signedDocumentUri;
}
