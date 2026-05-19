package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ChainOfCustodyRequestDTO;
import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;
import com.cts.trialledger.service.ChainOfCustodyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples/custody")
@RequiredArgsConstructor
public class ChainOfCustodyController {

    private final ChainOfCustodyService chainOfCustodyService;

    @PostMapping
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ChainOfCustodyResponseDTO transferSample(@Valid @RequestBody ChainOfCustodyRequestDTO dto) {
        return chainOfCustodyService.transferCustody(dto.getSampleId(), dto);
    }

    @GetMapping("/{cocId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ChainOfCustodyResponseDTO getCustodyById(@PathVariable Long cocId) {
        return chainOfCustodyService.getCustodyById(cocId);
    }

    @GetMapping("/sample/{sampleId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public List<ChainOfCustodyResponseDTO> getCustodyBySampleId(@PathVariable Long sampleId) {
        return chainOfCustodyService.getCustodyBySampleId(sampleId);
    }

    @GetMapping("/sample/{sampleId}/custody/latest")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ChainOfCustodyResponseDTO getLatestCustody(
            @PathVariable Long sampleId) {
        return chainOfCustodyService.getLatestCustody(sampleId);
    }
}