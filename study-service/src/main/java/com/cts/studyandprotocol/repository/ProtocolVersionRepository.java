package com.cts.studyandprotocol.repository;

import com.cts.studyandprotocol.entity.ProtocolVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProtocolVersionRepository extends JpaRepository<ProtocolVersion, Long> {

    List<ProtocolVersion> findByIsDeletedFalse();

    Optional<ProtocolVersion> findByProtocolIdAndIsDeletedFalse(Long protocolId);

    List<ProtocolVersion> findByStudy_StudyIdAndIsDeletedFalse(Long studyId);
}
