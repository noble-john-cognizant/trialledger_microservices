package com.cts.trialledger.provenance.repository;


import com.cts.trialledger.provenance.entity.DatasetSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DatasetSnapshotRepo extends JpaRepository<DatasetSnapshot,Long> {
    List<DatasetSnapshot> findByStudyId(Long studyId);
    List<DatasetSnapshot> findByStudyIdAndSnapshotDateGreaterThanEqualAndSnapshotDateLessThan(Long studyId, LocalDateTime start, LocalDateTime end);

}
