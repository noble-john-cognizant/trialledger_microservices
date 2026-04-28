package com.cts.studyandprotocol.repository;

import com.cts.studyandprotocol.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    List<Study> findByIsDeletedFalse();

    Optional<Study> findByStudyIdAndIsDeletedFalse(Long studyId);

    boolean existsByStudyIdAndIsDeletedFalse(Long studyId);

    boolean existsByProtocolNumberAndIsDeletedFalse(String protocolNumber);

    boolean existsByTitleAndIsDeletedFalse(String title);
}