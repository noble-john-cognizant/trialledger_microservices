package com.cts.adverseevent.repository;

import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.model.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdverseEventRepository extends JpaRepository<AdverseEvent, Long> {

    List<AdverseEvent> findByIsDeletedFalse();

    Optional<AdverseEvent> findByIdAndIsDeletedFalse(Long id);

    List<AdverseEvent> findByStudyIdAndIsDeletedFalse(Long studyId);

    List<AdverseEvent> findByParticipantIdAndIsDeletedFalse(Long participantId);

    List<AdverseEvent> findBySeverityAndIsDeletedFalse(Severity severity);
}
