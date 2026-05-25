package com.cts.trialledger.apigateway.mapper;

import com.cts.trialledger.apigateway.dto.AuditLogDTO;
import com.cts.trialledger.apigateway.entity.AuditLog;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class AuditMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static AuditLogDTO convertToAuditLogDTO(AuditLog auditLog) {
        return new AuditLogDTO(auditLog.getAuditId(),
                auditLog.getUserId(), auditLog.getAction(),
                auditLog.getResource(), auditLog.getTimestamp(),
                mapper.readValue(auditLog.getDetails(), new TypeReference<Map<String, Object>>() {}));
    }
}
