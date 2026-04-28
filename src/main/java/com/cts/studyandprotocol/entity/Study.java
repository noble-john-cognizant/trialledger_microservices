package com.cts.studyandprotocol.entity;

import com.cts.studyandprotocol.model.StudyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "study")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long studyId;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String sponsor;

    @Column(nullable = false, unique = true)
    private String protocolNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;

    @Column(nullable = false)
    private Boolean isDeleted = false;


    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProtocolVersion> protocols = new ArrayList<>();
}