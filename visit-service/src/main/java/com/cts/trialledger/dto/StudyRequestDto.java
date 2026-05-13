package com.cts.visit.dto;

import java.time.LocalDate;

/**
 * Local DTO mirroring study-service's StudyRequestDto.
 * Used only if visit-service ever needs to send study creation requests via Feign.
 */
public class StudyRequestDto {

    private String title;
    private String sponsor;
    private String protocolNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    public StudyRequestDto() {
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
}
