package com.cts.trialledger.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentResponseDTO {

    private Long participantId;
    private Long protocolId;
    private Integer versionNumber;
    private LocalDateTime consentDate;
    private String consentMethod;
    private String signedDocumentUri;
    private String status;


    private Long studyId;
    private Long totalParticipants;
    private Long enrolledCount;
    private Long withdrawnCount;
}