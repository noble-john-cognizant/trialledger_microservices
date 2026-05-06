package com.cts.adverseevent.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class ParticipantDto {
    private Long participantId;
    private Long studyId;
    private String externalId;
    private String name;
    private LocalDate dob;
    private String contactInfo;
    private String enrollmentStatus;
}
