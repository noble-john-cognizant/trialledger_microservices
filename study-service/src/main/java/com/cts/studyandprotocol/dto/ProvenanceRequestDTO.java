package com.cts.studyandprotocol.dto;

public record ProvenanceRequestDTO(
        String action,
        String entityType,
        Long performedBy,
        Long entityId,
        String metadata
) {
}
