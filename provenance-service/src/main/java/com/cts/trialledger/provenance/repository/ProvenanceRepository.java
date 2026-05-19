package com.cts.trialledger.provenance.repository;


import com.cts.trialledger.provenance.entity.ProvenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProvenanceRepository extends JpaRepository<ProvenanceRecord, Long> {
    List<ProvenanceRecord> findByPerformedAtBetween(LocalDateTime start, LocalDateTime end);
}
