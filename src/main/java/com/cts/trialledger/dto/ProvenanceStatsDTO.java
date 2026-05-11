package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvenanceStatsDTO {
    private Long studyId;
    private Long provenanceRecordCount;
}