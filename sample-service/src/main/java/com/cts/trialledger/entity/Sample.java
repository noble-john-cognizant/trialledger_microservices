package com.cts.trialledger.entity;

import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.model.SampleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sample")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sample_id")
    private Long sampleId;

    @NotNull
    @Column(name = "participant_id")
    private Long participantId;

    @NotNull
    @Column(name = "study_id", length = 36)
    private Long studyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sample_type", nullable = false)
    private SampleType sampleType;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "collected_by", length = 36)
    private String collectedBy;

    @Column(name = "initial_location")
    private String initialLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SampleStatus status;

}
