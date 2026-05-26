package com.cts.trialledger.client;

import com.cts.trialledger.dto.NotificationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public void createNotification(NotificationRequestDTO dto) {
        // No-op fallback
    }
}
