package com.cts.trialledger.apigateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action")
    private String action;          // HTTP method: GET, POST, PUT, DELETE

    @Column(name = "resource")
    private String resource;        // URL path: /api/orders/42

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;         // JSON string: status, duration, IP, etc.
}