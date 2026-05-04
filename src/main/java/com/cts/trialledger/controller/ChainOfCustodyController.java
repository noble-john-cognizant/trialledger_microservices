package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ChainOfCustodyRequestDTO;
import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;
import com.cts.trialledger.service.ChainOfCustodyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples/custody")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('TECHNICIAN')")
public class ChainOfCustodyController {

    private final ChainOfCustodyService chainOfCustodyService;

    @PostMapping
    public ChainOfCustodyResponseDTO transferSample(@Valid @RequestBody ChainOfCustodyRequestDTO dto) {
        return chainOfCustodyService.transferCustody(dto.getSampleId(), dto);
    }

    @GetMapping("/{cocId}")
    public ChainOfCustodyResponseDTO getCustodyById(@PathVariable Long cocId) {
        return chainOfCustodyService.getCustodyById(cocId);
    }

    @GetMapping("/sample/{sampleId}")
    public List<ChainOfCustodyResponseDTO> getCustodyBySampleId(@PathVariable Long sampleId) {
        return chainOfCustodyService.getCustodyBySampleId(sampleId);
    }

    @GetMapping("/sample/{sampleId}/custody/latest")
    public ChainOfCustodyResponseDTO getLatestCustody(
            @PathVariable Long sampleId) {
        return chainOfCustodyService.getLatestCustody(sampleId);
    }
}