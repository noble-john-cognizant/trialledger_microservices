package com.cts.trialledger.entity;

import com.cts.trialledger.model.ReportScope;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long studyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportScope scope;

    @Column(columnDefinition = "TEXT")
    private String parametersJson;

    @Column(columnDefinition = "TEXT")
    private String metricsJson;

    private LocalDateTime generatedAt;

    private String reportUri;
}
