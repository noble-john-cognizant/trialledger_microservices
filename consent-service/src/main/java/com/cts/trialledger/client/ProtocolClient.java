package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "STUDY-SERVICE",
contextId ="protocolClient",
        fallback = ProtocolClientFallback.class)
public interface ProtocolClient {

    @GetMapping("/api/studies/protocols/{protocolId}")
    Object getProtocolVersion(
            @PathVariable("protocolId") Long protocolId);


}

