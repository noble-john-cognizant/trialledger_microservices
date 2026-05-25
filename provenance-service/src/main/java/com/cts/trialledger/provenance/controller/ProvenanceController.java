package com.cts.trialledger.provenance.controller;


import com.cts.trialledger.provenance.dto.ProvenanceDTO;
import com.cts.trialledger.provenance.dto.ProvenanceRequestDTO;
import com.cts.trialledger.provenance.service.ProvenanceRecordService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/provenance")
public class ProvenanceController {

    private final ProvenanceRecordService provenanceRecordService;

    public ProvenanceController(ProvenanceRecordService provenanceRecordService) {
        this.provenanceRecordService = provenanceRecordService;
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE','ADMIN','PI', 'DATA_MANAGER', 'AUDITOR')")
    @GetMapping
    public ResponseEntity<Page<ProvenanceDTO>> getAuditTrail(@RequestParam(defaultValue = "0") Integer pageNumber, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(provenanceRecordService.getAllRecords(pageNumber, pageSize));
    }

    @PreAuthorize("hasAnyRole('COMPLIANCE','TECHNICIAN','COORDINATOR','ADMIN','PI', 'DATA_MANAGER', 'AUDITOR')")
    @PostMapping
    public ResponseEntity<String> recordProvenanceData(@RequestBody ProvenanceRequestDTO dto) {
        provenanceRecordService.recordData(dto.action(), dto.entityType(), dto.performedBy(), dto.entityId(), dto.metadata());
        return ResponseEntity.ok("success");
    }
}
