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

<<<<<<< HEAD
    @Column(name = "approved_by_id")
=======
    @Column(name = "approved_by_id", nullable = false)
>>>>>>> 09b0807c705d15c7b53b6bb00bf12bc6fa4e2ec9
    private Long approvedById;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProtocolStatus status;

    @Column(nullable = false)
    private Boolean isDeleted = false;
}
