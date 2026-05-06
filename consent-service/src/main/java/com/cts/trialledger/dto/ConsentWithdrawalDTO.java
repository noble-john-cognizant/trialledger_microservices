package com.cts.trialledger.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentWithdrawalDTO {

    @NotNull
    private Long consentId;

    @NotNull
    private Long withdrawnBy;

    @NotBlank
    private String reason;

    private String effectOnData;
}
