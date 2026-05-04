package com.cts.trialledger.controller;

import com.cts.trialledger.dto.AssayRunRequestDTO;
import com.cts.trialledger.dto.AssayRunResponseDTO;
import com.cts.trialledger.service.AssayRunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/samples/assays")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('TECHNICIAN')")
public class AssayRunController {
    private final AssayRunService assayRunService;

    @PostMapping
    public AssayRunResponseDTO createAssayRun(@Valid @RequestBody AssayRunRequestDTO dto) {

        return assayRunService.createAssayRun(dto);
    }

    @GetMapping("/sample/{sampleId}")
    public List<AssayRunResponseDTO> getAssaysBySample(@PathVariable Long sampleId) {
        return assayRunService.getAssaysBySample(sampleId);
    }

    @GetMapping("/operator/{operatorId}")
    public List<AssayRunResponseDTO> getAssaysByOperator(@PathVariable Long operatorId) {
        return assayRunService.getAssaysByOperator(operatorId);
    }

    @GetMapping("/instrument/{instrumentId}")
    public List<AssayRunResponseDTO> getAssaysByInstrument(@PathVariable Long instrumentId) {
        return assayRunService.getAssaysByInstrument(instrumentId);
    }

    @GetMapping
    public List<AssayRunResponseDTO> getAllAssayRuns() {
        return assayRunService.getAllAssayRuns();
    }

    @GetMapping("/{assayId}")
    public AssayRunResponseDTO getAssayRunById(@PathVariable Long assayId) {
        return assayRunService.getAssayRunById(assayId);
    }
}