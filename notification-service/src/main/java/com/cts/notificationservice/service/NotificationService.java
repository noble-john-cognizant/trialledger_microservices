package com.cts.notificationservice.service;

import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.dto.NotificationResponseDTO;
import com.cts.notificationservice.entity.Notification;
import com.cts.notificationservice.exception.ResourceNotFoundException;
import com.cts.notificationservice.mapper.NotificationMapper;
import com.cts.notificationservice.model.NotificationCategory;
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

        // If a participantUserId is provided and is different from the actor,
        // also create a copy for the participant so they see it in their feed
        if (dto.getParticipantUserId() != null
                && !dto.getParticipantUserId().equals(dto.getUserId())) {

            log.info("Also notifying participantUserId: {}", dto.getParticipantUserId());

            Notification participantCopy = Notification.builder()
                    .userId(dto.getParticipantUserId())
                    .entityId(dto.getEntityId())
                    .message(dto.getMessage())
                    .category(dto.getCategory())
                    .status(NotificationStatus.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(participantCopy);
        }

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

    // GET UNREAD BY USER
    public List<NotificationResponseDTO> getUnreadByUser(Long userId) {

        log.info("Fetching UNREAD notifications for userId: {}", userId);

        return repository.findByUserIdAndStatus(userId, NotificationStatus.UNREAD)
                .stream()
                .map(NotificationMapper::mapToDTO)
                .toList();
    }

    // GET BY CATEGORY
    public List<NotificationResponseDTO> getByCategory(NotificationCategory category) {

        log.info("Fetching notifications by category: {}", category);

        return repository.findByCategory(category)
                .stream()
                .map(NotificationMapper::mapToDTO)
                .toList();
    }

    // GET BY USER AND CATEGORY
    public List<NotificationResponseDTO> getByUserAndCategory(
            Long userId, NotificationCategory category) {

        log.info("Fetching notifications for userId: {} and category: {}",
                userId, category);

        return repository.findByUserIdAndCategory(userId, category)
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
                                "Notification not found with ID: " + id));

        notification.setStatus(NotificationStatus.READ);

        Notification updated = repository.save(notification);

        return NotificationMapper.mapToDTO(updated);
    }

    // MARK ALL AS READ FOR USER
    public String markAllAsReadForUser(Long userId) {

        log.info("Marking all notifications as READ for userId: {}", userId);

        List<Notification> unread = repository.findByUserIdAndStatus(
                userId, NotificationStatus.UNREAD);

        unread.forEach(n -> n.setStatus(NotificationStatus.READ));
        repository.saveAll(unread);

        log.info("Marked {} notifications as read for userId: {}", unread.size(), userId);

        return "Marked " + unread.size() + " notification(s) as read for userId: " + userId;
    }

    // DELETE NOTIFICATION
    public String deleteNotification(Long id) {

        log.info("Deleting notification with ID: {}", id);

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with ID: " + id));

        repository.delete(notification);

        log.info("Notification deleted successfully with ID: {}", id);

        return "Notification with ID " + id + " deleted successfully";
    }

    // GET BY ID
    public NotificationResponseDTO getById(Long id) {

        log.info("Fetching notification with ID: {}", id);

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with ID: " + id));

        return NotificationMapper.mapToDTO(notification);
    }
}
