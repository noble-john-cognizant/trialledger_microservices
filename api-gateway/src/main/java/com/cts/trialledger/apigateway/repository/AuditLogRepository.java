package com.cts.trialledger.apigateway.repository;

import com.cts.trialledger.apigateway.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByAction(String action);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :from AND :to")
    List<AuditLog> findByTimestampBetween(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}