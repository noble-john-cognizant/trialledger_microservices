package com.cts.studyandprotocol.dto;

import com.cts.studyandprotocol.model.StudyStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyResponseDto {
    private Long studyId;
    private String title;
    private String sponsor;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private StudyStatus status;
    private Boolean isDeleted;
}