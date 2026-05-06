package com.cts.trialledger.exception;

public class SampleNotFoundException extends RuntimeException {

    public SampleNotFoundException(Long sampleId) {
        super("Sample not found with ID: " + sampleId);
    }
}