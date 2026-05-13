package com.cts.visit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class SourceDataRequestDto {

    @NotNull(message = "Visit ID is required")
    @Positive(message = "Visit ID must be a positive value")
    private Long visitId;

    @NotNull(message = "Collected By (User ID) is required")
    @Positive(message = "Collected By must be a positive value")
    private Long collectedBy;

    @NotBlank(message = "Data type is required")
    private String dataType;

    @NotBlank(message = "Data URI (file path) is required")
    private String dataUri;

    @NotNull(message = "Collected date and time is required")
    private LocalDateTime collectedAt;

    public SourceDataRequestDto() {
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
}
