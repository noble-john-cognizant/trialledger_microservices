package com.cts.trialledger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

//public record ProvenanceDTO(
//        Long provId,
//        String entityType,
//        Long entityId,
//        String action,
//        Long performedBy,
//        LocalDateTime performedAt,
//        Map<String,Object> metadataJson) {
//}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvenanceDTO {

    private Long studyId;
    private Long provenanceRecordCount;
}