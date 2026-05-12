package com.cts.notificationservice.repository;

import com.cts.notificationservice.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleRepository
        extends JpaRepository<AlertRule, Long> {
}