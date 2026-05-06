package com.cts.trialledger.repository;

import com.cts.trialledger.entity.AssayRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssayRunRepository extends JpaRepository<AssayRun, Long> {

    List<AssayRun> findBySample_SampleId(Long sampleId);

    List<AssayRun> findByOperatorId(Long operatorId);

    List<AssayRun> findByInstrumentId(Long instrumentId);

    Long countBySample_StudyId(Long studyId);
}