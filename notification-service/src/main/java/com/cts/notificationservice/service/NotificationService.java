package com.cts.notificationservice.service;

import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.dto.NotificationResponseDTO;
import com.cts.notificationservice.entity.Notification;
import com.cts.notificationservice.exception.ResourceNotFoundException;
import com.cts.notificationservice.mapper.NotificationMapper;
import com.cts.notificationservice.model.NotificationStatus;
import com.cts.notificationservice.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    // CREATE NOTIFICATION
    public NotificationResponseDTO createNotification(
            NotificationRequestDTO dto) {

        log.info("Creating notification for userId: {}",
                dto.getUserId());

        Notification notification = Notification.builder()
                .userId(dto.getUserId())
                .entityId(dto.getEntityId())
                .message(dto.getMessage())
                .category(dto.getCategory())
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = repository.save(notification);

        log.info("Notification created successfully with ID: {}",
                saved.getNotificationId());

        return NotificationMapper.mapToDTO(saved);
    }

    // GET ALL
    public List<NotificationResponseDTO> getAllNotifications() {

        log.info("Fetching all notifications");

        return repository.findAll()
                .stream()
                .map(NotificationMapper::mapToDTO)
                .toList();
    }

    // GET BY USER
    public List<NotificationResponseDTO> getByUser(Long userId) {

        log.info("Fetching notifications for userId: {}",
                userId);

        return repository.findByUserId(userId)
                .stream()
                .map(NotificationMapper::mapToDTO)
                .toList();
    }

    // MARK AS READ
    public NotificationResponseDTO markAsRead(Long id) {

        log.info("Marking notification as READ: {}", id);

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found"));

        notification.setStatus(NotificationStatus.READ);

        Notification updated = repository.save(notification);

        return NotificationMapper.mapToDTO(updated);
    }
}