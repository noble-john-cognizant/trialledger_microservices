package com.cts.trialledger.apigateway.service;

import com.cts.trialledger.apigateway.dto.AuditLogDTO;
import com.cts.trialledger.apigateway.entity.AuditLog;
import com.cts.trialledger.apigateway.mapper.AuditMapper;
import com.cts.trialledger.apigateway.model.UserDetails;
import com.cts.trialledger.apigateway.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    // Called after every request completes
    public void saveAudit(HttpServletRequest request,
                          int statusCode,
                          long durationMs) {
        try {
            Long userId = extractUserId(request);

            Map<String, Object> details = new LinkedHashMap<>();
            details.put("statusCode", statusCode);
            details.put("durationMs", durationMs);
            details.put("clientIp", getClientIp(request));
            details.put("userAgent", request.getHeader("User-Agent"));
            details.put("queryString", request.getQueryString());

            AuditLog log = AuditLog.builder()
                    .userId(userId)
                    .action(request.getMethod())
                    .resource(request.getRequestURI())
                    .timestamp(LocalDateTime.now())
                    .details(objectMapper.writeValueAsString(details))
                    .build();

            auditLogRepository.save(log);

        } catch (Exception e) {
            log.error("Failed to save audit log", e);
        }
    }

    private Long extractUserId(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal()) && auth.getPrincipal() instanceof UserDetails user) {
            return user.getUserId();
        }
        return 0L;
    }

    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    // ---------- Paged queries ----------

    public Page<AuditLogDTO> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(AuditMapper::convertToAuditLogDTO);
    }

    public Page<AuditLogDTO> getAllAuditLogsByUserId(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(AuditMapper::convertToAuditLogDTO);
    }

    public Page<AuditLogDTO> getAllAuditLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable).map(AuditMapper::convertToAuditLogDTO);
    }

    public Page<AuditLogDTO> getAllAuditLogsBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(from, to, pageable).map(AuditMapper::convertToAuditLogDTO);
    }
}
