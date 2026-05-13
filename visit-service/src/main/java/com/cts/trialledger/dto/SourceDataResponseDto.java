package com.cts.visit.dto;

import java.time.LocalDateTime;

public class SourceDataResponseDto {

    private Long sourceId;
    private Long visitId;
    private Long collectedBy;
    private String dataType;
    private String dataUri;
    private LocalDateTime collectedAt;
    private String hash;

    public SourceDataResponseDto() {
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
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
