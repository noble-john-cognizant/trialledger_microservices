package com.cts.notificationservice.service;

import com.cts.notificationservice.dto.AlertRuleRequestDTO;
import com.cts.notificationservice.dto.AlertRuleResponseDTO;
import com.cts.notificationservice.dto.NotificationRequestDTO;
import com.cts.notificationservice.entity.AlertRule;
import com.cts.notificationservice.exception.ResourceNotFoundException;
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
    public AlertRuleResponseDTO create(AlertRuleRequestDTO dto) {

        log.info("Creating alert rule: {}", dto.getName());

        AlertRule rule = AlertRule.builder()
                .name(dto.getName())
                .triggerExpression(dto.getTriggerExpression())
                .severity(dto.getSeverity())
                .recipientsJson(dto.getRecipientsJson())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        AlertRule saved = repository.save(rule);

        log.info("Alert rule created with ID: {}", saved.getRuleId());

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

    // GET BY ID
    public AlertRuleResponseDTO getById(Long ruleId) {

        log.info("Fetching alert rule with ID: {}", ruleId);

        AlertRule rule = repository.findById(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert rule not found with ID: " + ruleId));

        return AlertRuleMapper.mapToDTO(rule);
    }

    // UPDATE RULE
    public AlertRuleResponseDTO update(Long ruleId, AlertRuleRequestDTO dto) {

        log.info("Updating alert rule with ID: {}", ruleId);

        AlertRule rule = repository.findById(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert rule not found with ID: " + ruleId));

        rule.setName(dto.getName());
        rule.setTriggerExpression(dto.getTriggerExpression());
        rule.setSeverity(dto.getSeverity());
        rule.setRecipientsJson(dto.getRecipientsJson());
        rule.setActive(dto.getActive());

        AlertRule updated = repository.save(rule);

        log.info("Alert rule updated successfully");

        return AlertRuleMapper.mapToDTO(updated);
    }

    // TOGGLE ACTIVE STATUS
    public AlertRuleResponseDTO toggleActive(Long ruleId) {

        log.info("Toggling active status for alert rule ID: {}", ruleId);

        AlertRule rule = repository.findById(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert rule not found with ID: " + ruleId));

        rule.setActive(!rule.getActive());

        AlertRule updated = repository.save(rule);

        log.info("Alert rule ID {} is now {}",
                ruleId, updated.getActive() ? "ACTIVE" : "INACTIVE");

        return AlertRuleMapper.mapToDTO(updated);
    }

    // DELETE RULE
    public String delete(Long ruleId) {

        log.info("Deleting alert rule with ID: {}", ruleId);

        AlertRule rule = repository.findById(ruleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Alert rule not found with ID: " + ruleId));

        repository.delete(rule);

        log.info("Alert rule deleted successfully with ID: {}", ruleId);

        return "Alert rule with ID " + ruleId + " deleted successfully";
    }

    // TRIGGER ALERT (evaluates and creates notification)
    public void evaluateAndTrigger(
            Long userId,
            Long entityId,
            String message,
            NotificationCategory category) {

        log.info("Evaluating alert trigger for userId: {}, category: {}",
                userId, category);

        NotificationRequestDTO dto =
                NotificationRequestDTO.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .message(message)
                        .category(category)
                        .build();

        notificationService.createNotification(dto);

        log.info("Alert triggered successfully for userId: {}", userId);
    }
}