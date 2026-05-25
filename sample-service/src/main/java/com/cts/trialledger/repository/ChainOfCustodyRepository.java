package com.cts.trialledger.repository;

import com.cts.trialledger.entity.ChainOfCustody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChainOfCustodyRepository extends JpaRepository<ChainOfCustody, Long> {

    List<ChainOfCustody> findBySample_SampleId(Long sampleId);

    Optional<ChainOfCustody> findTopBySample_SampleIdOrderByTransferAtDescCocIdDesc(Long sampleId);

    Long countBySample_StudyId(Long studyId);
}