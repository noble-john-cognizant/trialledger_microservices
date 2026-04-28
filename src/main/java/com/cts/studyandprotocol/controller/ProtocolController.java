package com.cts.studyandprotocol.controller;

import com.cts.studyandprotocol.dto.ProtocolVersionRequestDto;
import com.cts.studyandprotocol.dto.ProtocolVersionResponseDto;
import com.cts.studyandprotocol.model.ProtocolStatus;
import com.cts.studyandprotocol.service.ProtocolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;

    @PostMapping("/{studyId}/protocols")
    public ResponseEntity<ProtocolVersionResponseDto> uploadProtocol(
            @PathVariable Long studyId,
            @Valid @RequestBody ProtocolVersionRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(protocolService.uploadProtocol(studyId, dto));
    }

    @GetMapping("/{studyId}/protocols")
    public ResponseEntity<List<ProtocolVersionResponseDto>> getProtocols(@PathVariable Long studyId) {
        return ResponseEntity.ok(protocolService.getProtocolByStudy(studyId));
    }

    @GetMapping("/protocols")
    public ResponseEntity<List<ProtocolVersionResponseDto>> getAllProtocols() {
        return ResponseEntity.ok(protocolService.getAllProtocols());
    }

    @GetMapping("/protocols/{protocolId}")
    public ResponseEntity<ProtocolVersionResponseDto> getProtocolById(@PathVariable Long protocolId) {
        return ResponseEntity.ok(protocolService.getProtocolById(protocolId));
    }

    @PatchMapping("/protocols/{protocolId}/status")
    public ResponseEntity<String> updateProtocolStatus(
            @PathVariable Long protocolId,
            @RequestParam ProtocolStatus protocolStatus) {
        return ResponseEntity.ok(protocolService.updateProtocolStatus(protocolId, protocolStatus));
    }

    @DeleteMapping("/{studyId}/protocols/{protocolId}")
    public ResponseEntity<String> deleteProtocol(
            @PathVariable Long studyId,
            @PathVariable Long protocolId) {
        return ResponseEntity.ok(protocolService.deleteProtocol(studyId, protocolId));
    }
}