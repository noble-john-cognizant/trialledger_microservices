package com.cts.studyandprotocol.dto;

import com.cts.studyandprotocol.model.StudyStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
