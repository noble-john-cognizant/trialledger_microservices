package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "studyandprotocol-service",
contextId ="protocolClient")
public interface ProtocolClient {

    @GetMapping("/api/studies/protocols/{protocolId}/versions/{version}")
    Object getProtocolVersion(
            @PathVariable("protocolId") Long protocolId,
            @PathVariable("version") String version);


}

