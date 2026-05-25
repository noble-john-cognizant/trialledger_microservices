package com.cts.trialledger.provenance.client;

import com.cts.trialledger.provenance.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class VisitClientFallback implements VisitClient {
    @Override
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getVisitsByStudy(Long studyId) {
        ApiResponseDto<List<Map<String, Object>>> body = new ApiResponseDto<>();
        return ResponseEntity.ok(body);
    }
}
