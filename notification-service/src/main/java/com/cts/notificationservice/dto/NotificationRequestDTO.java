package com.cts.notificationservice.dto;

import com.cts.notificationservice.model.NotificationCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {

    private Long userId;

    private Long entityId;

    private String message;

    private NotificationCategory category;
}