package com.cts.visit.controller;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.dto.SourceDataRequestDto;
import com.cts.visit.dto.SourceDataResponseDto;
import com.cts.visit.service.SourceDataService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sourcedata")
public class SourceDataController {

    private final SourceDataService sourceDataService;

    public SourceDataController(SourceDataService sourceDataService) {
        this.sourceDataService = sourceDataService;
    }

    // 1. Capture source data for a visit
    @PostMapping("/visit")
    public ResponseEntity<ApiResponseDto<SourceDataResponseDto>> addSourceData(
            @Valid @RequestBody SourceDataRequestDto sourceDataRequestDto) {

        SourceDataResponseDto response = sourceDataService.addSourceData(sourceDataRequestDto);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Source data captured successfully", response)
        );
    }

    // 2. Get source data by source data ID
    @GetMapping("/{sourceDataId}")
    public ResponseEntity<ApiResponseDto<SourceDataResponseDto>> getSourceDataById(
            @PathVariable Long sourceDataId) {

        SourceDataResponseDto response = sourceDataService.getSourceDataById(sourceDataId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Source data fetched successfully", response)
        );
    }

    // 3. Verify source data integrity (SHA-256 hash verification)
    @GetMapping("/verify/{sourceDataId}")
    public ResponseEntity<ApiResponseDto<Boolean>> verifySourceDataHash(
            @PathVariable Long sourceDataId) {

        boolean integrityVerified = sourceDataService.verifySourceDataHash(sourceDataId);

        return ResponseEntity.ok(
                new ApiResponseDto<>(
                        integrityVerified ? "SUCCESS" : "FAILURE",
                        integrityVerified
                                ? "Source data integrity verified. File is unchanged."
                                : "Source data integrity failed. File may be tampered.",
                        integrityVerified
                )
        );
    }

    // 4. Get all source data by visitId
    @GetMapping("/byVisit/{visitId}")
    public ResponseEntity<ApiResponseDto<List<SourceDataResponseDto>>> getSourceDataByVisitId(
            @PathVariable Long visitId) {

        List<SourceDataResponseDto> response = sourceDataService.getSourceDataByVisitId(visitId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Source data fetched successfully", response)
        );
    }
}
