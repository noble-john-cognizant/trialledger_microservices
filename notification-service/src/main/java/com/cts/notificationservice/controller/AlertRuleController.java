package com.cts.notificationservice.controller;

import com.cts.notificationservice.dto.AlertRuleRequestDTO;
import com.cts.notificationservice.dto.AlertRuleResponseDTO;
import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.service.AlertRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertRuleService service;

    @PostMapping
    public ResponseEntity<AlertRuleResponseDTO> create(
            @RequestBody AlertRuleRequestDTO dto) {

        return ResponseEntity.ok(
                service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AlertRuleResponseDTO>>
    getAll() {

        return ResponseEntity.ok(
                service.getAll());
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerAlert(

            @RequestParam Long userId,

            @RequestParam Long entityId,

            @RequestParam String message,

            @RequestParam NotificationCategory category) {

        service.evaluateAndTrigger(
                userId,
                entityId,
                message,
                category
        );

        return ResponseEntity.ok(
                "Alert Triggered Successfully");
    }
}