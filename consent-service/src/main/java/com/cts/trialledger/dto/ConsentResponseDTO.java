package com.cts.trialledger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentResponseDTO {

    private Long consentId;
    private Long participantId;
    private Long protocolId;
    private String versionNumber;
    private String status;
}
