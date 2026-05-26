package com.cts.adverseevent.dto;

import com.cts.adverseevent.model.NotificationCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequestDTO {

    private Long userId;

    private Long entityId;

    private String message;

    private String category;
}