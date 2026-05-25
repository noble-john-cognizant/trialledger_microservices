package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantResponseDTO {

    private Long participantId;
    private Long studyId;
    private String externalId;
    private String name;
    private LocalDate dob;
    private String contactInfo;
    private String enrollmentStatus;

}