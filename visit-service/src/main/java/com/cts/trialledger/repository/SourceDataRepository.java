package com.cts.visit.repository;

import com.cts.visit.entity.SourceData;
import com.cts.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SourceDataRepository extends JpaRepository<SourceData, Long> {

    List<SourceData> findByVisit(Visit visit);

    // Field navigation: visit.visitId
    List<SourceData> findByVisit_VisitId(Long visitId);
}
