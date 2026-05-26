package com.cts.trialledger.provenance.client;

import com.cts.trialledger.provenance.client.fallback.VisitClientFallback;
import com.cts.trialledger.provenance.dto.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "VISIT-SERVICE", path = "/api/visits", fallback = VisitClientFallback.class)
public interface VisitClient {
    @GetMapping("/study/{studyId}")
    ResponseEntity<ApiResponseDto<List<Map<String,Object>>>> getVisitsByStudy(@PathVariable Long studyId);

}
