package com.cts.trialledger.provenance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_package")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditPackage {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "package_id")
        private Long packageId;

        @Column(name = "study_id", nullable = false)
        private Long studyId;

        @Column(nullable = false, name = "period_start")
        private LocalDate periodStart;

        @Column(nullable = false, name = "period_end")
        private LocalDate periodEnd;

        @Column(name = "package_uri", nullable = false, columnDefinition = "TEXT")
        private String packageUri;

        @Column(name = "generated_at")
        private LocalDateTime generatedAt ;

        @Column(name = "contents_json", nullable = false, columnDefinition = "JSON")
        private String contentsJSON;
}
