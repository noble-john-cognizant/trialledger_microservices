package com.cts.trialledger.controller;

import com.cts.trialledger.dto.ReportRequestDTO;
import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.model.ReportScope;
import com.cts.trialledger.service.ReportServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceImpl reportServiceImpl;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE','DATA_MANAGER')")
    public ReportResponseDTO generateReport(@Valid @RequestBody ReportRequestDTO dto) {
        return reportServiceImpl.generateReport(dto);
    }

    @GetMapping("/{reportId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ReportResponseDTO getReport(@PathVariable Long reportId) {
        return reportServiceImpl.getReportById(reportId);
    }

    @GetMapping("/study/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public List<ReportResponseDTO> getReportsByStudy(@PathVariable Long studyId) {
        return reportServiceImpl.getReportsByStudy(studyId);
    }

    @GetMapping("/scope/{scope}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public List<ReportResponseDTO> getReportsByScope(@PathVariable ReportScope scope) {
        return reportServiceImpl.getReportsByScope(scope);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public List<ReportResponseDTO> getAllReports() {
        return reportServiceImpl.getAllReports();
    }

    @GetMapping("/{reportId}/download")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
        ReportResponseDTO report = reportServiceImpl.getReportById(reportId);

        if (report.getReportUri() == null || report.getReportUri().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        java.nio.file.Path filePath = java.nio.file.Paths.get(report.getReportUri().trim());
        if (!java.nio.file.Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);
        String filename = filePath.getFileName().toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}