package com.cts.visit.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "source_data")
public class SourceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_id")
    private Long sourceId;

    // Visit is in the same service, so JPA relationship is fine
    @ManyToOne
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    // User ID who collected the source data (User lives in another service)
    @Column(name = "collected_by", nullable = false)
    private Long collectedBy;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "data_uri")
    private String dataUri;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "hash")
    private String hash;

    public SourceData() {
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public Long getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(Long collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataUri() {
        return dataUri;
    }

    public void setDataUri(String dataUri) {
        this.dataUri = dataUri;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
