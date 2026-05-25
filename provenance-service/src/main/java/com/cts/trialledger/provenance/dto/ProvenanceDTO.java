package com.cts.trialledger.provenance.dto;


import java.time.LocalDateTime;
import java.util.Map;

public record ProvenanceDTO(
        Long provId,
        String entityType,
        Long entityId,
        String action,
        Long performedBy,
        LocalDateTime performedAt,
        Map<String,Object> metadataJson
) {
}
