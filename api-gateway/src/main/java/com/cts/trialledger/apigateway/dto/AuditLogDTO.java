package com.cts.trialledger.apigateway.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditLogDTO(
         Long auditId,
         Long userId,
         String action,
         String resource,
         LocalDateTime timestamp,
         Map<String , Object> details
) {
}
