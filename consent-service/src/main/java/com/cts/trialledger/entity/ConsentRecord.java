package com.cts.trialledger.entity;

import com.cts.trialledger.model.ConsentMethod;

import com.cts.trialledger.model.ConsentStatus;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participantId;

    @Column(nullable = false)
    private Long protocolId;

    @Column(nullable = false)
    private String versionNumber;

    private LocalDateTime consentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentMethod consentMethod;

    @Column(nullable = false)
    private String signedDocumentUri;

    @Column(nullable = false)
    private String signatureHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

}