package com.cts.trialledger.provenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provenance_record")
public class ProvenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prov_id")
    private Long provId;

    @Column(nullable = false, length = 50, name = "entity_type")
    private String entityType; // e.g., "PARTICIPANT", "SAMPLE", "CONSENT"

    @Column(nullable = false, name = "entity_id")
    private Long entityId; // The ID of the record being changed

    @Column(nullable = false,length = 100)
    private String action; // e.g., "CREATED", "UPDATED", "WITHDRAWN"

    @Column(nullable = false, name = "performed_by")
    private Long performedBy; // UserID of the actor

    @Column(name = "performed_at")
    private LocalDateTime performedAt = LocalDateTime.now();

    @Column(columnDefinition = "JSON", name = "metadata_json")
    private String metadataJson;
}
