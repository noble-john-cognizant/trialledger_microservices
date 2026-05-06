package com.cts.trialledger.provenance.dto;


import java.time.LocalDate;


public record AuditPackageDTO(
        Long studyId,
        LocalDate startDate,
        LocalDate endDate
) {
}
