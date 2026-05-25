package com.cts.trialledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sample_storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SampleStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long storageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Column(name = "freezer_id")
    private Long freezerId;

    @Column(name = "shelf")
    private String shelf;

    @Column(name = "box")
    private String box;

    @Column(name = "position")
    private String position;

    @Column(name = "stored_at")
    private LocalDateTime storedAt;

    @Column(name = "retrieved_at")
    private LocalDateTime retrievedAt;
}