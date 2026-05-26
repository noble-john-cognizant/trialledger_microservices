package com.cts.visit.client;

import com.cts.visit.dto.NotificationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public void createNotification(NotificationRequestDTO dto) {
        // No-op fallback
    }
}
