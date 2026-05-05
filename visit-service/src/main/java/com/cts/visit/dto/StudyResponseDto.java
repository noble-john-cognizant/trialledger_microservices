package com.cts.visit.dto;

import java.time.LocalDate;

/**
 * Local DTO used by visit-service's Feign client to deserialize
 * the response from study-service.
 *
 * Keeping `status` as String avoids importing the StudyStatus enum
 * from study-service (no shared library).
 */
public class StudyResponseDto {

    private Long studyId;
    private String title;
    private String sponsor;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isDeleted;

    public StudyResponseDto() {
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
