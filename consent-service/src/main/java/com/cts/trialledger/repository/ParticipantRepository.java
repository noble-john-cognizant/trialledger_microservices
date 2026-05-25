package com.cts.trialledger.repository;

import com.cts.trialledger.entity.Participant;

import com.cts.trialledger.model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // Fetch all the participants for the specific study id
    List<Participant> findByStudyId(Long studyId);

    Long countByStudyId(Long studyId);

    Long countByStudyIdAndEnrollmentStatus(Long studyId, EnrollmentStatus enrollmentStatus);

    boolean existsByContactInfo(String contactInfo);

    /** Look up a participant by their unique phone (stored as contactInfo). */
    Optional<Participant> findByContactInfo(String contactInfo);
}