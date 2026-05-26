package com.cts.trialledger.apigateway.controller;

import com.cts.trialledger.apigateway.dto.AuditLogDTO;
import com.cts.trialledger.apigateway.service.AuditService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditLogDTO> getAllAuditLogs() {
        return auditService.getAllAuditLogs();
    }

    @GetMapping("action/{action}")
    public List<AuditLogDTO> getAllAuditLogsByAction(@PathVariable String action) {
        return auditService.getAllAuditLogsByAction(action);
    }

    @GetMapping("userId/{userId}")
    public List<AuditLogDTO> getAllAuditLogsByUser(@PathVariable Long userId) {
        return auditService.getAllAuditLogsByUserId(userId);
    }

    @GetMapping("find-by-range")
    public List<AuditLogDTO> getAllAuditLogsBetween(@RequestParam LocalDateTime from, @RequestParam LocalDateTime to) {
        return auditService.getAllAuditLogsBetween(from, to);
    }
}
