package com.cts.trialledger.exception;

public class ReportNotFoundException extends ResourceNotFoundException {

    public ReportNotFoundException(Long id) {
        super("Report not found with ID: " + id);
    }
}