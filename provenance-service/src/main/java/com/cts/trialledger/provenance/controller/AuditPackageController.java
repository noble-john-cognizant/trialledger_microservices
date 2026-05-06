package com.cts.trialledger.provenance.controller;


import com.cts.trialledger.provenance.dto.AuditPackageDTO;
import com.cts.trialledger.provenance.entity.AuditPackage;
import com.cts.trialledger.provenance.service.AuditPackageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/audit-packages")
public class AuditPackageController {

    private final AuditPackageService auditPackageService;

    public AuditPackageController(AuditPackageService auditPackageService) {
        this.auditPackageService = auditPackageService;
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE','ADMIN')")
    @PostMapping
    public AuditPackage generateAuditPackage(@RequestBody AuditPackageDTO auditPackageDTO) throws Exception {
        return auditPackageService.generatePackage(auditPackageDTO.studyId(), auditPackageDTO.startDate(), auditPackageDTO.endDate());
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE','ADMIN','AUDITOR')")
    @GetMapping
    public List<AuditPackage> getAllAuditPackage(@RequestParam Long studyId) {
        return auditPackageService.getAllAuditPackages(studyId);
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE','ADMIN','AUDITOR')")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadAuditPackage(@PathVariable Long id) throws IOException {
        Resource resource = auditPackageService.getAuditPackage(id);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Prepare headers for file download
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip")) // Use "application/json" for snapshots
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilePath() + "\"")
                .body(resource);
    }
}
