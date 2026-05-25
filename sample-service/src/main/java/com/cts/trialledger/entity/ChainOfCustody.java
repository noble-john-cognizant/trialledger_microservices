package com.cts.trialledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chain_of_custody")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChainOfCustody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coc_id")
    private Long cocId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Column(name = "from_user", length = 36)
    private String fromUser;

    @Column(name = "to_user", length = 36)
    private String toUser;

    @Column(name = "transfer_at")
    private LocalDateTime transferAt;

    @Column(name = "from_location")
    private String fromLocation;

    @Column(name = "to_location")
    private String toLocation;

    @Column(name = "notes")
    private String notes;
}