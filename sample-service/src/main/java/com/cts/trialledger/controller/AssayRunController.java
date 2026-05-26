package com.cts.trialledger.controller;

import com.cts.trialledger.dto.AssayRunRequestDTO;
import com.cts.trialledger.dto.AssayRunResponseDTO;
import com.cts.trialledger.service.AssayRunService;
import org.springframework.core.io.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples/assays")
@RequiredArgsConstructor
public class AssayRunController {
    private final AssayRunService assayRunService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TECHNICIAN')")
    public AssayRunResponseDTO createAssayRun(@Valid @RequestBody AssayRunRequestDTO dto) {

        return assayRunService.createAssayRun(dto);
    }

    @GetMapping("/sample/{sampleId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public List<AssayRunResponseDTO> getAssaysBySample(@PathVariable Long sampleId) {
        return assayRunService.getAssaysBySample(sampleId);
    }

    @GetMapping("/operator/{operatorId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public List<AssayRunResponseDTO> getAssaysByOperator(@PathVariable Long operatorId) {
        return assayRunService.getAssaysByOperator(operatorId);
    }

    @GetMapping("/instrument/{instrumentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public List<AssayRunResponseDTO> getAssaysByInstrument(@PathVariable Long instrumentId) {
        return assayRunService.getAssaysByInstrument(instrumentId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public List<AssayRunResponseDTO> getAllAssayRuns() {
        return assayRunService.getAllAssayRuns();
    }

    @GetMapping("/{assayId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public AssayRunResponseDTO getAssayRunById(@PathVariable Long assayId) {
        return assayRunService.getAssayRunById(assayId);
    }

    @GetMapping("/{assayId}/result")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','DATA_MANAGER')")
    public ResponseEntity<Resource> downloadResult(@PathVariable Long assayId) {
        Resource resource = assayRunService.downloadResult(assayId);
        String filename = resource.getFilename() != null ? resource.getFilename() : "assay-result.json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}