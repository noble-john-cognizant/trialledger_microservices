package com.cts.studyandprotocol.exception;

public class ProtocolNotFoundException extends RuntimeException {
    public ProtocolNotFoundException(Long protocolId) {
        super("Protocol not found with ID: " + protocolId);
    }
}