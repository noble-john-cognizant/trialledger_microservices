package com.cts.trialledger.client;

import com.cts.trialledger.dto.KPIResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "KPI-SERVICE", path = "/api/kpis")
public interface KPIClient {

    @PatchMapping("/{kpiId}/refresh")
    void refreshKPIValue(@PathVariable("kpiId") Long kpiId,
                         @RequestParam("studyId") Long studyId);

    @GetMapping("/period/{period}")
    List<KPIResponseDTO> getKPIsByPeriod(@PathVariable("period") String period);

    @PatchMapping("/refresh/study/{studyId}")
    void refreshAllKPIsForStudy(@PathVariable("studyId") Long studyId);
}