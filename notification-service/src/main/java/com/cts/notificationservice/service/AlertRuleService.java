package com.cts.notificationservice.service;

import com.cts.notificationservice.dto.AlertRuleRequestDTO;
import com.cts.notificationservice.dto.AlertRuleResponseDTO;
import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.entity.AlertRule;
import com.cts.notificationservice.mapper.AlertRuleMapper;
import com.cts.notificationservice.model.NotificationCategory;
import com.cts.notificationservice.repository.AlertRuleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertRuleService {

    private final AlertRuleRepository repository;

    private final NotificationService notificationService;

    // CREATE RULE
    public AlertRuleResponseDTO create(
            AlertRuleRequestDTO dto) {

        log.info("Creating alert rule: {}", dto.getName());

        AlertRule rule = AlertRule.builder()
                .name(dto.getName())
                .triggerExpression(dto.getTriggerExpression())
                .severity(dto.getSeverity())
                .recipientsJson(dto.getRecipientsJson())
                .active(dto.getActive())
                .build();

        AlertRule saved = repository.save(rule);

        log.info("Alert rule created successfully");

        return AlertRuleMapper.mapToDTO(saved);
    }

    // GET ALL RULES
    public List<AlertRuleResponseDTO> getAll() {

        log.info("Fetching all alert rules");

        return repository.findAll()
                .stream()
                .map(AlertRuleMapper::mapToDTO)
                .toList();
    }

    // TRIGGER ALERT
    public void evaluateAndTrigger(
            Long userId,
            Long entityId,
            String message,
            NotificationCategory category) {

        log.info("Evaluating alert trigger");

        NotificationRequestDTO dto =
                NotificationRequestDTO.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .message(message)
                        .category(category)
                        .build();

        notificationService.createNotification(dto);

        log.info("Alert triggered successfully");
    }
}