package com.cts.trialledger.repository;

import com.cts.trialledger.entity.ConsentRecord;
import com.cts.trialledger.model.ConsentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentRepository extends JpaRepository<ConsentRecord, Long> {

    List<ConsentRecord> findByParticipantId_ParticipantId(Long participantId);

    List<ConsentRecord> findByParticipantId_StudyId(Long studyId);

    boolean existsByParticipantId_ParticipantIdAndProtocolIdAndStatus(
            Long participantId,
            Long protocolId,
            ConsentStatus status
    );
}