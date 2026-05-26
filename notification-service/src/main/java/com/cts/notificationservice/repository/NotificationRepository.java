package com.cts.notificationservice.repository;

import com.cts.notificationservice.entity.Notification;
import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByCategory(NotificationCategory category);

    List<Notification> findByUserIdAndCategory(Long userId, NotificationCategory category);
}
