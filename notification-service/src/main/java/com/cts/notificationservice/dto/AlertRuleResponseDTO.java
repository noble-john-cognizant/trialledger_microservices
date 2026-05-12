package com.cts.notificationservice.dto;

import com.cts.notificationservice.model.AlertSeverity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRuleResponseDTO {

    private Long ruleId;

    private String name;

    private String triggerExpression;

    private AlertSeverity severity;

    private String recipientsJson;

    private Boolean active;
}
