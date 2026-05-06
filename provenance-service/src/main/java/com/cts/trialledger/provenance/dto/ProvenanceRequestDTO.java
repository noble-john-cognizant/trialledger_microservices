package com.cts.trialledger.provenance.dto;

public record ProvenanceRequestDTO(
        String action,
        String entityType,
        Long performedBy,
        Long entityId,
        String metadata
) {
}
