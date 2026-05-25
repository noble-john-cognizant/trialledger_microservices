package com.cts.notificationservice.entity;

import com.cts.notificationservice.model.AlertSeverity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    private String name;

    private String triggerExpression;

    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;

    @Column(columnDefinition = "TEXT")
    private String recipientsJson;

    private Boolean active;
}
