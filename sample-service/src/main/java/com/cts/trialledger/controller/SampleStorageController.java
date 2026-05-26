package com.cts.trialledger.controller;

import com.cts.trialledger.dto.SampleStorageRequestDTO;
import com.cts.trialledger.dto.SampleStorageResponseDTO;
import com.cts.trialledger.service.SampleStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleStorageController {

    private final SampleStorageService sampleStorageService;

    @PostMapping("/{sampleId}/storage")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public SampleStorageResponseDTO storeSample(@PathVariable Long sampleId, @Valid @RequestBody SampleStorageRequestDTO dto) {
        return sampleStorageService.storeSample(sampleId, dto);
    }

    @GetMapping("/storage/{storageId}/retrieve")
    @PreAuthorize("hasAnyRole('PI','TECHNICIAN','DATA_MANAGER','TECHNICIAN')")
    public SampleStorageResponseDTO retrieveSample(@PathVariable Long storageId) {
        return sampleStorageService.retrieveSample(storageId);
    }

    @GetMapping("/{sampleId}/storage")
    @PreAuthorize("hasAnyRole('PI','TECHNICIAN','DATA_MANAGER')")
    public List<SampleStorageResponseDTO> getStorageHistory(@PathVariable Long sampleId) {
        return sampleStorageService.getStorageHistory(sampleId);
    }
}