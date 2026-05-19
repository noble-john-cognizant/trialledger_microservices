package com.cts.notificationservice.mapper;

import com.cts.notificationservice.dto.AlertRuleResponseDTO;
import com.cts.notificationservice.entity.AlertRule;

public class AlertRuleMapper {

    public static AlertRuleResponseDTO mapToDTO(
            AlertRule rule) {

        return AlertRuleResponseDTO.builder()
                .ruleId(rule.getRuleId())
                .name(rule.getName())
                .triggerExpression(rule.getTriggerExpression())
                .severity(rule.getSeverity())
                .recipientsJson(rule.getRecipientsJson())
                .active(rule.getActive())
                .build();
    }
}