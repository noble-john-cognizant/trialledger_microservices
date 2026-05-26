package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ApiResponseDTO;
import com.cts.trialledger.dto.SampleRequestDTO;
import com.cts.trialledger.dto.SampleResponseDTO;
import com.cts.trialledger.dto.SampleStatsDTO;
import com.cts.trialledger.model.SampleStatus;
import com.cts.trialledger.repository.AssayRunRepository;
import com.cts.trialledger.repository.ChainOfCustodyRepository;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.service.SampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;
    private final SampleRepository sampleRepository;
    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final AssayRunRepository assayRunRepository;

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

    @PatchMapping("/{sampleId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','COORDINATOR')")
    public ResponseEntity<SampleResponseDTO> updateStatus(
            @PathVariable Long sampleId,
            @RequestParam SampleStatus status) {
        return ResponseEntity.ok(sampleService.updateStatus(sampleId, status));
    }

    @GetMapping("/{sampleId}/full")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ResponseEntity<ApiResponseDTO> getSampleFull(@PathVariable Long sampleId) {
        return ResponseEntity.ok(sampleService.getSampleFull(sampleId));
    }

    @GetMapping("/stats/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public SampleStatsDTO getSampleStats(@PathVariable Long studyId) {
        log.info("[SampleController] getSampleStats() called | studyId={}", studyId);

        return SampleStatsDTO.builder()
                .studyId(studyId)
                .totalSamples(sampleRepository.countByStudyId(studyId))
                .collectedCount(sampleRepository.countByStudyIdAndStatus(studyId, SampleStatus.COLLECTED))
                .inAnalysisCount(sampleRepository.countByStudyIdAndStatus(studyId, SampleStatus.IN_ANALYSIS))
                .completedCount(sampleRepository.countByStudyIdAndStatus(studyId, SampleStatus.COMPLETED))
                .custodyEventCount(chainOfCustodyRepository.countBySample_StudyId(studyId))
                .assayRunCount(assayRunRepository.countBySample_StudyId(studyId))
                .build();
    }
}