package com.cts.trialledger.client;

import com.cts.trialledger.dto.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE",
        fallback = NotificationClientFallback.class)
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void createNotification(@RequestBody NotificationRequestDTO dto);
}