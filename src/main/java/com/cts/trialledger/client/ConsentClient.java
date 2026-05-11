package com.cts.trialledger.client;

import com.cts.trialledger.client.dto.EnrollmentStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "consent-service", path = "/api/participants")
public interface ConsentClient {

    @GetMapping("/stats/{studyId}")
    EnrollmentStatsDTO getEnrollmentStats(@PathVariable("studyId") Long studyId);

}