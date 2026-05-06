package com.cts.trialledger.dto;

import com.cts.trialledger.model.EnrollmentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
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
    private EnrollmentStatus enrollmentStatus;
}
