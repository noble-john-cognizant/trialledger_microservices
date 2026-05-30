package com.cts.trialledger.apigateway.controller;

import com.cts.trialledger.apigateway.dto.AuditLogDTO;
import com.cts.trialledger.apigateway.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * All endpoints return a Spring {@link Page}. The client controls paging
 * with the standard {@code ?page=N&size=M&sort=field,dir} query params.
 * Default is page 0, size 20, sorted by timestamp DESC.
 */
@RestController
@RequestMapping("api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public Page<AuditLogDTO> getAllAuditLogs(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.getAllAuditLogs(pageable);
    }

    @GetMapping("action/{action}")
    public Page<AuditLogDTO> getAllAuditLogsByAction(
            @PathVariable String action,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.getAllAuditLogsByAction(action, pageable);
    }

    @GetMapping("userId/{userId}")
    public Page<AuditLogDTO> getAllAuditLogsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.getAllAuditLogsByUserId(userId, pageable);
    }

    @GetMapping("find-by-range")
    public Page<AuditLogDTO> getAllAuditLogsBetween(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        return auditService.getAllAuditLogsBetween(from, to, pageable);
    }
}
