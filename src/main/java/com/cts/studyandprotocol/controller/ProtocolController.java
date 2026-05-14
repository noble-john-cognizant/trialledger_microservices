package com.cts.studyandprotocol.controller;

import com.cts.studyandprotocol.dto.ProtocolVersionRequestDto;
import com.cts.studyandprotocol.dto.ProtocolVersionResponseDto;
import com.cts.studyandprotocol.model.ProtocolStatus;
import com.cts.studyandprotocol.service.ProtocolService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;

    @PostMapping("/{studyId}/protocols")
    @PreAuthorize("hasAnyRole('ADMIN','PI')")
    public ResponseEntity<ProtocolVersionResponseDto> uploadProtocol(
            @PathVariable Long studyId,
            @Valid @RequestBody ProtocolVersionRequestDto dto) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(protocolService.uploadProtocol(studyId, dto));
    }

    @GetMapping("/{studyId}/protocols")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','COORDINATOR')")
    public ResponseEntity<List<ProtocolVersionResponseDto>> getProtocols(@PathVariable Long studyId) {
        return ResponseEntity.ok(protocolService.getProtocolByStudy(studyId));
    }

    @GetMapping("/protocols")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','COORDINATOR')")
    public ResponseEntity<List<ProtocolVersionResponseDto>> getAllProtocols() {
        return ResponseEntity.ok(protocolService.getAllProtocols());
    }

    @GetMapping("/protocols/{protocolId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','COORDINATOR')")
    public ResponseEntity<ProtocolVersionResponseDto> getProtocolById(@PathVariable Long protocolId) {
        return ResponseEntity.ok(protocolService.getProtocolById(protocolId));
    }

    @PatchMapping("/protocols/{protocolId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE')")
    public ResponseEntity<String> updateProtocolStatus(
            @PathVariable Long protocolId,
            @RequestParam ProtocolStatus protocolStatus) throws JsonProcessingException {
        return ResponseEntity.ok(protocolService.updateProtocolStatus(protocolId, protocolStatus));
    }

    @PatchMapping("/protocols/{protocolId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE')")
    public ResponseEntity<String> approveProtocol(@PathVariable Long protocolId)
            throws JsonProcessingException {
        return ResponseEntity.ok(protocolService.approveProtocol(protocolId));
    }

    @DeleteMapping("/{studyId}/protocols/{protocolId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COMPLIANCE')")
    public ResponseEntity<String> deleteProtocol(
            @PathVariable Long studyId,
            @PathVariable Long protocolId) throws JsonProcessingException {
        return ResponseEntity.ok(protocolService.deleteProtocol(studyId, protocolId));
    }
}
