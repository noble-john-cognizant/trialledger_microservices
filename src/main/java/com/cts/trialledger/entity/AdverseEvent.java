package com.cts.trialledger.entity;

import com.cts.trialledger.model.AEStatus;
import com.cts.trialledger.model.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adverse_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdverseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ae_id")
    private Long id;

    // Cross-service: participant lives in participant-service.
    @Column(nullable = false)
    private Long participantId;

    // Cross-service: study lives in study-service.
    @Column(nullable = false)
    private Long studyId;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false)
    private String description;

    // Cross-service: user lives in identity-service.
    @Column(name = "reported_by_id", nullable = false)
    private Long reportedById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AEStatus status;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    // Same service — JPA relationship retained.
    @OneToMany(mappedBy = "adverseEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AEFollowUp> followUps = new ArrayList<>();
}
