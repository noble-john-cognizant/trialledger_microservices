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
//import org.springframework.security.access.prepost.PreAuthorize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('TECHNICIAN')")
public class SampleController {

    private final SampleService sampleService;
    private final SampleRepository sampleRepository;
    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final AssayRunRepository assayRunRepository;

    @PostMapping
    public SampleResponseDTO createSample(@Valid @RequestBody SampleRequestDTO dto) {
        return sampleService.createSample(dto);
    }

    @GetMapping("/{sampleId}")
    public SampleResponseDTO getSample(@PathVariable Long sampleId) {
        return sampleService.getSampleById(sampleId);
    }

    @GetMapping("/participant/{participantId}")
    public List<SampleResponseDTO> getSamplesByParticipant(@PathVariable Long participantId) {
        return sampleService.getSamplesByParticipant(participantId);
    }

    @GetMapping("/study/{studyId}")
    public List<SampleResponseDTO> getSamplesByStudy(@PathVariable Long studyId) {
        return sampleService.getSamplesByStudy(studyId);
    }

    @GetMapping("/status/{status}")
    public List<SampleResponseDTO> getSamplesByStatus(@PathVariable SampleStatus status) {
        return sampleService.getSamplesByStatus(status);
    }

    @GetMapping
    public List<SampleResponseDTO> getAllSamples() {
        return sampleService.getAllSamples();
    }

    @GetMapping("/{sampleId}/full")
    public ResponseEntity<ApiResponseDTO> getSampleFull(@PathVariable Long sampleId) {
        return ResponseEntity.ok(sampleService.getSampleFull(sampleId));
    }

    @GetMapping("/stats/{studyId}")
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