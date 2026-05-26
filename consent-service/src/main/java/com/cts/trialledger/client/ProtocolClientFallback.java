package com.cts.trialledger.client;

import org.springframework.stereotype.Component;

@Component
public class ProtocolClientFallback implements ProtocolClient {
    @Override
    public Object getProtocolVersion(Long protocolId) {
        return null;
    }
}
