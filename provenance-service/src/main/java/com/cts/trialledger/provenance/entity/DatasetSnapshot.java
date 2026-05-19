package com.cts.trialledger.provenance.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_snapshot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "snapshot_id")
    private Long snapshotId;

    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "snapshot_date")
    private LocalDateTime snapshotDate = LocalDateTime.now();

    @Column(name = "snapshot_uri", nullable = false, columnDefinition = "TEXT")
    private String snapshotUri; // Path to storage

    @Column(nullable = false)
    private String hash; // SHA-256 for tamper detection

    @Column(columnDefinition = "JSON", nullable = false, name = "included_entites_json")
    private String includedEntitiesJson; // List of tables included (e.g., ["Participant", "Sample"])
}
