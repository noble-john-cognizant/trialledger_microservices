package com.cts.trialledger.repository;

import com.cts.trialledger.entity.Report;
import com.cts.trialledger.model.ReportScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStudyId(Long studyId);

    List<Report> findByScope(ReportScope scope);
}