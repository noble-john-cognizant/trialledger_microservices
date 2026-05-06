package com.cts.trialledger.exception;

public class AssayRunNotFoundException extends RuntimeException {

    public AssayRunNotFoundException(Long assayId) {
        super("Assay run not found with ID: " + assayId);
    }
}