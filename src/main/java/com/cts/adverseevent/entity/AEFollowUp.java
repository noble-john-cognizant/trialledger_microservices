package com.cts.adverseevent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ae_follow_up")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AEFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_up_id")
    private Long id;

    // Same service — JPA relationship retained.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ae_id", nullable = false)
    private AdverseEvent adverseEvent;

    @Column(nullable = false)
    private String actionTaken;

    // Cross-service: user lives in identity-service.
    @Column(name = "performed_by_id", nullable = false)
    private Long performedById;

    @Column(nullable = true)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
