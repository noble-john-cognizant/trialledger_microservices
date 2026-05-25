package com.cts.notificationservice.controller;

import com.cts.notificationservice.dto.AlertRuleRequestDTO;
import com.cts.notificationservice.dto.AlertRuleResponseDTO;
import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertRuleResponseDTO> create(
            @RequestBody AlertRuleRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE')")
    public ResponseEntity<List<AlertRuleResponseDTO>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{ruleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE')")
    public ResponseEntity<AlertRuleResponseDTO> getById(
            @PathVariable Long ruleId) {

        return ResponseEntity.ok(service.getById(ruleId));
    }

    @PutMapping("/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertRuleResponseDTO> update(
            @PathVariable Long ruleId,
            @RequestBody AlertRuleRequestDTO dto) {

        return ResponseEntity.ok(service.update(ruleId, dto));
    }

    @PutMapping("/{ruleId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertRuleResponseDTO> toggleActive(
            @PathVariable Long ruleId) {

        return ResponseEntity.ok(service.toggleActive(ruleId));
    }

    @DeleteMapping("/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(
            @PathVariable Long ruleId) {

        return ResponseEntity.ok(service.delete(ruleId));
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerAlert(
            @RequestParam Long userId,
            @RequestParam Long entityId,
            @RequestParam String message,
            @RequestParam NotificationCategory category) {

        service.evaluateAndTrigger(userId, entityId, message, category);

        return ResponseEntity.ok("Alert Triggered Successfully");
    }
}