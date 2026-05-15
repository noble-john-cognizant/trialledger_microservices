package com.cts.trialledger.repository;

import com.cts.trialledger.entity.AEFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AEFollowUpRepository extends JpaRepository<AEFollowUp, Long> {

    List<AEFollowUp> findByAdverseEvent_IdAndIsDeletedFalse(Long aeId);

    Optional<AEFollowUp> findByIdAndIsDeletedFalse(Long id);
}
