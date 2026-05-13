package com.cts.visit.repository;

import com.cts.visit.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    // Fetch all visits for a given participant id
    List<Visit> findByParticipantId(Long participantId);

    // Fetch all visits for a given study id
    List<Visit> findByStudyId(Long studyId);
}
