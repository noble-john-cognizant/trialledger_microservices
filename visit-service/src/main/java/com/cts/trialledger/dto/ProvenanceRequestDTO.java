package com.cts.visit.dto;

public record ProvenanceRequestDTO(
        String action,
        String entityType,
        Long performedBy,
        Long entityId,
        String metadata
) {
}
