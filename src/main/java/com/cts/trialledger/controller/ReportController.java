package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ReportRequestDTO;
import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.model.ReportScope;
import com.cts.trialledger.service.ReportServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceImpl reportServiceImpl;

    @PostMapping
    public ReportResponseDTO generateReport(@Valid @RequestBody ReportRequestDTO dto) {
        return reportServiceImpl.generateReport(dto);
    }

    @GetMapping("/{reportId}")
    public ReportResponseDTO getReport(@PathVariable Long reportId) {
        return reportServiceImpl.getReportById(reportId);
    }

    @GetMapping("/study/{studyId}")
    public List<ReportResponseDTO> getReportsByStudy(@PathVariable Long studyId) {
        return reportServiceImpl.getReportsByStudy(studyId);
    }

    @GetMapping("/scope/{scope}")
    public List<ReportResponseDTO> getReportsByScope(@PathVariable ReportScope scope) {
        return reportServiceImpl.getReportsByScope(scope);
    }

    @GetMapping
    public List<ReportResponseDTO> getAllReports() {
        return reportServiceImpl.getAllReports();
    }
}