package com.cts.trialledger.repository;

import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.model.SampleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleRepository extends JpaRepository<Sample,Long> {

    List<Sample> findByStudyId(Long studyId);

    List<Sample> findByParticipantId(Long participantId);

    List<Sample> findByStatus(SampleStatus status);

    Long countByStudyId(Long studyId);

    Long countByStudyIdAndStatus(Long studyId, SampleStatus status);

    @Query("SELECT COUNT(s) FROM Sample s WHERE s.status = 'COLLECTED' AND s.studyId = :studyId")
    Long countCollectedByStudy(@Param("studyId") Long studyId);
}