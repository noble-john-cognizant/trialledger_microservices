package com.cts.adverseevent.client;

import com.cts.adverseevent.dto.NotificationRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationClientFallback implements NotificationClient {
    @Override
    public void createNotification(NotificationRequestDTO dto) {
        // No-op fallback: notification failure shouldn't block the main flow
    }
}
