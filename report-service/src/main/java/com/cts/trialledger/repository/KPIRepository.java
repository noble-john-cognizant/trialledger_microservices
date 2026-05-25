package com.cts.trialledger.repository;

import com.cts.trialledger.entity.KPI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KPIRepository extends JpaRepository<KPI, Long> {
    List<KPI> findByReportingPeriod(String reportingPeriod);

}