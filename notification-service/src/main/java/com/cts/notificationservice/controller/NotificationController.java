package com.cts.notificationservice.controller;

import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.dto.NotificationResponseDTO;
import com.cts.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(
            @RequestBody NotificationRequestDTO dto) {

        return ResponseEntity.ok(service.createNotification(dto));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAll() {

        return ResponseEntity.ok(service.getAllNotifications());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getByUser(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.markAsRead(id));
    }
}

