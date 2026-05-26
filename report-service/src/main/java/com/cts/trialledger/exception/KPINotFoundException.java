package com.cts.trialledger.exception;


public class KPINotFoundException extends ResourceNotFoundException {
    public KPINotFoundException(Long id) {
        super("KPI not found with ID: " + id);
    }
}