package com.cts.trialledger.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity

@Table(name = "consent_withdrawals")

@Getter

@Setter

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class ConsentWithdrawal {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long withdrawalId;

    //  FK to Consent

    @Column(nullable = false, unique = true)

    private Long consentId;

    @Column(nullable = false)

    private Long withdrawnBy;

    @Column(nullable = false)

    private String reason;

    private String effectOnData;

    @Column(nullable = false)

    private LocalDateTime withdrawnAt;

}
