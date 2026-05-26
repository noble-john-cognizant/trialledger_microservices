package com.cts.trialledger.controller;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.service.KPIService;
import com.cts.trialledger.service.KPIServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIServiceImpl kpiServiceImpl;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI')")
    public KPIResponseDTO createKPI(@Valid @RequestBody KPIRequestDTO dto) throws JsonProcessingException {
        return kpiServiceImpl.createKPI(dto);
    }

    @GetMapping("/{kpiId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE','DATA_MANAGER')")
    public KPIResponseDTO getKPI(@PathVariable Long kpiId) {
        return kpiServiceImpl.getKPIById(kpiId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE','DATA_MANAGER')")
    public List<KPIResponseDTO> getAllKPIs() {
        return kpiServiceImpl.getAllKPIs();
    }

    @GetMapping("/period/{period}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE','DATA_MANAGER')")
    public List<KPIResponseDTO> getKPIsByPeriod(@PathVariable String period) {
        return kpiServiceImpl.getKPIsByPeriod(period);
    }
}