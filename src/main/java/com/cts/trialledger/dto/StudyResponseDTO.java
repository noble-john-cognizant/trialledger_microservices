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
public class StudyResponseDTO {
    private Long studyId;
    private String title;
    private String sponsor;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}