package com.cts.trialledger.repository;

import com.cts.trialledger.entity.ConsentWithdrawal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentWithdrawalRepository extends JpaRepository<ConsentWithdrawal, Long> {

    boolean existsByConsentId(Long consentId);

}