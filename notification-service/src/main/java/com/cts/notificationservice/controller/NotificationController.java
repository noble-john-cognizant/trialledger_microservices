package com.cts.notificationservice.controller;

import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.dto.NotificationResponseDTO;
import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    // Called internally by other microservices via Feign
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(
            @RequestBody NotificationRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNotification(dto));
    }

    // Admin/Compliance: get all notifications
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE')")
    public ResponseEntity<List<NotificationResponseDTO>> getAll() {

        return ResponseEntity.ok(service.getAllNotifications());
    }

    // Get notifications by ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // Get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getByUser(userId));
    }

    // Get UNREAD notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.getUnreadByUser(userId));
    }

    // Get notifications by category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE', 'COORDINATOR')")
    public ResponseEntity<List<NotificationResponseDTO>> getByCategory(
            @PathVariable NotificationCategory category) {

        return ResponseEntity.ok(service.getByCategory(category));
    }

    // Get notifications for a user filtered by category
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUserAndCategory(
            @PathVariable Long userId,
            @PathVariable NotificationCategory category) {

        return ResponseEntity.ok(service.getByUserAndCategory(userId, category));
    }

    // Mark a single notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponseDTO> markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.markAsRead(id));
    }

    // Mark ALL notifications as read for a user
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsRead(
            @PathVariable Long userId) {

        return ResponseEntity.ok(service.markAllAsReadForUser(userId));
    }

    // Delete a notification
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.deleteNotification(id));
    }
}

