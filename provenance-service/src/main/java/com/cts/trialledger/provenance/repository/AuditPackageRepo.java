package com.cts.trialledger.provenance.repository;


import com.cts.trialledger.provenance.entity.AuditPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AuditPackageRepo extends JpaRepository<AuditPackage, Long> {
    List<AuditPackage> findByStudyId(Long studyId);
}
