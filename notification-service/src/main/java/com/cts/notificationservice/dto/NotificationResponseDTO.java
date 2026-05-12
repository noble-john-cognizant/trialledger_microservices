package com.cts.notificationservice.dto;

import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.model.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private Long notificationId;

    private Long userId;

    private Long entityId;

    private String message;

    private NotificationCategory category;

    private NotificationStatus status;

    private LocalDateTime createdAt;
}