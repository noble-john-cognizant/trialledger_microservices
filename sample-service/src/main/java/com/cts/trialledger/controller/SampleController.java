package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ApiResponseDTO;
import com.cts.trialledger.dto.SampleRequestDTO;
import com.cts.trialledger.dto.SampleResponseDTO;
import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.service.SampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINATOR','TECHNICIAN')")
    public SampleResponseDTO createSample(@Valid @RequestBody SampleRequestDTO dto) {
        return sampleService.createSample(dto);
    }

    @GetMapping("/{sampleId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public SampleResponseDTO getSample(@PathVariable Long sampleId) {
        return sampleService.getSampleById(sampleId);
    }

    @GetMapping("/participant/{participantId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER','PARTICIPANT')")
    public List<SampleResponseDTO> getSamplesByParticipant(@PathVariable Long participantId) {
        return sampleService.getSamplesByParticipant(participantId);
    }

    @GetMapping("/study/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','TECHNICIAN','DATA_MANAGER')")
    public List<SampleResponseDTO> getSamplesByStudy(@PathVariable Long studyId) {
        return sampleService.getSamplesByStudy(studyId);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER','PARTICIPANT')")
    public List<SampleResponseDTO> getSamplesByStatus(@PathVariable SampleStatus status) {
        return sampleService.getSamplesByStatus(status);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public List<SampleResponseDTO> getAllSamples() {
        return sampleService.getAllSamples();
    }

    @GetMapping("/{sampleId}/full")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getSampleFull(@PathVariable Long sampleId) {
        return ResponseEntity.ok(sampleService.getSampleFull(sampleId));
    }
}