package com.cts.trialledger.controller;

import com.cts.trialledger.dto.KPIRequestDTO;
import com.cts.trialledger.dto.KPIResponseDTO;
import com.cts.trialledger.service.KPIServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpis")
@RequiredArgsConstructor
public class KPIController {

    private final KPIServiceImpl kpiServiceImpl;

    @PostMapping
    public KPIResponseDTO createKPI(@Valid @RequestBody KPIRequestDTO dto) {
        return kpiServiceImpl.createKPI(dto);
    }

    @GetMapping("/{kpiId}")
    public KPIResponseDTO getKPI(@PathVariable Long kpiId) {
        return kpiServiceImpl.getKPIById(kpiId);
    }

    @GetMapping
    public List<KPIResponseDTO> getAllKPIs() {
        return kpiServiceImpl.getAllKPIs();
    }

    @GetMapping("/period/{period}")
    public List<KPIResponseDTO> getKPIsByPeriod(@PathVariable String period) {
        return kpiServiceImpl.getKPIsByPeriod(period);
    }
}