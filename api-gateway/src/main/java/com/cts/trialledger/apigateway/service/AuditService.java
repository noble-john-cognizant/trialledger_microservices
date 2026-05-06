package com.cts.trialledger.apigateway.service;

import com.cts.trialledger.apigateway.dto.AuditLogDTO;
import com.cts.trialledger.apigateway.entity.AuditLog;
import com.cts.trialledger.apigateway.entity.User;
import com.cts.trialledger.apigateway.mapper.AuditMapper;
import com.cts.trialledger.apigateway.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
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

            // Build a details map → serialize to JSON string
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("statusCode", statusCode);
            details.put("durationMs", durationMs);
            details.put("clientIp", getClientIp(request));
            details.put("userAgent", request.getHeader("User-Agent"));
            details.put("queryString", request.getQueryString());

            AuditLog log = AuditLog.builder()
                    .userId(userId)                                      // WHO made the request
                    .action(request.getMethod())                         // WHAT method: GET/POST...
                    .resource(request.getRequestURI())                   // WHERE: /api/orders/42
                    .timestamp(LocalDateTime.now())                      // WHEN
                    .details(objectMapper.writeValueAsString(details))   // HOW: metadata JSON
                    .build();

            auditLogRepository.save(log);

        } catch (Exception e) {
            // Never let audit failure crash the main request
            log.error("Failed to save audit log", e);
        }
    }

    // Extract userId from Spring Security context (already authenticated by your security config)
    private Long extractUserId(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal()) && auth.getPrincipal() instanceof User user) {
            return user.getUserId();
        }
        return 0L;
    }

    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public List<AuditLogDTO> getAllAuditLogs() {
        return auditLogRepository.findAll().stream().map(AuditMapper::convertToAuditLogDTO).toList();
    }

    public List<AuditLogDTO> getAllAuditLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId).stream().map(AuditMapper::convertToAuditLogDTO).toList();
    }

    public List<AuditLogDTO> getAllAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action).stream().map(AuditMapper::convertToAuditLogDTO).toList();
    }

    public List<AuditLogDTO> getAllAuditLogsBetween(LocalDateTime from, LocalDateTime to) {
        return auditLogRepository.findByTimestampBetween(from, to).stream().map(AuditMapper::convertToAuditLogDTO).toList();
    }
}