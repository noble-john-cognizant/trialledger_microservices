package com.cts.trialledger.dto;

public record ProvenanceRequestDTO(
        String action,
        String entityType,
        Long performedBy,
        Long entityId,
        String metadata
) {
}
