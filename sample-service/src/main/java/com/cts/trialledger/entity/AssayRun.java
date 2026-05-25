package com.cts.trialledger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "assay_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssayRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assay_id")
    private Long assayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id")
    private Sample sample;

    @Column(name = "instrument_id")
    private Long instrumentId;

    @NotNull
    @Column(name = "operator_id", length = 36)
    private Long operatorId;

    @Column(name = "run_date")
    private LocalDateTime runDate;

    @Column(name = "protocol_ref")
    private String protocolRef;

    @Column(name = "result_uri")
    private String resultUri;

    @Column(name = "metadata_json", columnDefinition = "json")
    private String metadataJson;
}