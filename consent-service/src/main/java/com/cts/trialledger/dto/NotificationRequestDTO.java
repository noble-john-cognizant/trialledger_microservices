package com.cts.trialledger.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequestDTO {
    private Long userId;
    private Long entityId;
    private String message;
    private String category;
    private String status;
}
