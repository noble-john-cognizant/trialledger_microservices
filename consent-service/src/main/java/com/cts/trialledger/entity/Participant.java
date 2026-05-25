package com.cts.trialledger.entity;

import com.cts.trialledger.model.EnrollmentStatus;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;

@Entity

@Table(name = "participants")

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    private String externalId;

    private String name;

    private LocalDate dob;

    @Column(nullable = false, unique = true)
    private String contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus enrollmentStatus;

}