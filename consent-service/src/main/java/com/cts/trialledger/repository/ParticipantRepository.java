package com.cts.trialledger.repository;

import com.cts.trialledger.entity.Participant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // Fetch all the participants for the specific study id
    List<Participant> findByStudyId(Long studyId);

    Long countByStudyId(Long studyId);

    Long countByStudyIdAndEnrollmentStatus(Long studyId, String enrollmentStatus);

    boolean existsByContactInfo(String contactInfo);
}