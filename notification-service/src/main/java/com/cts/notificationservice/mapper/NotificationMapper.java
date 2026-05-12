package com.cts.notificationservice.mapper;

import com.cts.notificationservice.dto.NotificationResponseDTO;
import com.cts.notificationservice.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO mapToDTO(Notification notification) {

        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .entityId(notification.getEntityId())
                .message(notification.getMessage())
                .category(notification.getCategory())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
