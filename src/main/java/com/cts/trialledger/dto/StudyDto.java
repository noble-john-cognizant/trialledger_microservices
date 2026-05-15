package com.cts.trialledger.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Mirror of StudyResponseDto from study-service.
 * Used to deserialize JSON responses from Feign calls.
 *
 * Note: status is String here (not the StudyStatus enum) to avoid
 * coupling AE service to study-service's enum class. The JSON value
 * (e.g., "ACTIVE", "PLANNED") deserializes fine into a String.
 */
@Getter
@Setter
@NoArgsConstructor
public class StudyDto {
    private Long studyId;
    private String title;
    private String sponsor;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isDeleted;
}
