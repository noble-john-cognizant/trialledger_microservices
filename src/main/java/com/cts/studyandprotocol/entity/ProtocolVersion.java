package com.cts.studyandprotocol.entity;

import com.cts.studyandprotocol.model.ProtocolStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "protocol_version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "protocol_id")
    private Long protocolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "version_number", nullable = false)
    private String versionNumber;

    @Column(nullable = false)
    private String documentUrl;

    private LocalDate effectiveDate;

    @Column(name = "approved_by_id")
    private Long approvedById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolStatus status;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
