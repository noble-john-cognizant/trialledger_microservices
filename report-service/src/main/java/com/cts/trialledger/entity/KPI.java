package com.cts.trialledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kpi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kpiId;

    private Long studyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String definition;

    private Double target;

    private Double currentValue;

    private String reportingPeriod;
}