package com.cts.trialledger.repository;

import com.cts.trialledger.entity.SampleStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleStorageRepository
        extends JpaRepository<SampleStorage, Long> {

    List<SampleStorage> findBySample_SampleId(Long sampleId);
}
