package com.cts.trialledger.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "STUDY-SERVICE",
contextId ="protocolClient")
public interface ProtocolClient {

    @GetMapping("/api/studies/protocols/{protocolId}/versions/{version}")
    Object getProtocolVersion(
            @PathVariable("protocolId") Long protocolId,
            @PathVariable("version") String version);


}

