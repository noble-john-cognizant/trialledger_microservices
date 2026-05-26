package com.cts.adverseevent.controller;

import com.cts.adverseevent.api.ApiMessage;
import com.cts.adverseevent.dto.AdverseEventRequestDto;
import com.cts.adverseevent.dto.AdverseEventResponseDto;
import com.cts.adverseevent.dto.ApiResponseDto;
import com.cts.adverseevent.model.Severity;
import com.cts.adverseevent.service.AdverseEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adverse-events")
public class AdverseEventController {

    private final AdverseEventService adverseEventService;

    public AdverseEventController(AdverseEventService adverseEventService) {
        this.adverseEventService = adverseEventService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public ResponseEntity<List<AdverseEventResponseDto>> getAllAE() {
        return ResponseEntity.ok(adverseEventService.getAllAE());
    }

    @GetMapping("/{aeId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public ResponseEntity<AdverseEventResponseDto> getAEById(@PathVariable Long aeId) {
        return ResponseEntity.ok(adverseEventService.getAEById(aeId));
    }

    @GetMapping("/study/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public ResponseEntity<List<AdverseEventResponseDto>> getByStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(adverseEventService.getAEByStudy(studyId));
    }

    @GetMapping("/participant/{participantId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public ResponseEntity<List<AdverseEventResponseDto>> getByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(adverseEventService.getAEByParticipant(participantId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PI','COORDINATOR','TECHNICIAN')")
    public ResponseEntity<ApiMessage> createAE(@Valid @RequestBody AdverseEventRequestDto dto) throws JsonProcessingException {
        AdverseEventResponseDto created = adverseEventService.createAE(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiMessage("Adverse event created successfully with ID: " + created.getAeId()));
    }

    @PatchMapping("/{aeId}/status")
    @PreAuthorize("hasAnyRole('PI','COMPLIANCE')")
    public ResponseEntity<AdverseEventResponseDto> updateStatus(@PathVariable Long aeId,
                                                                @RequestParam String status) throws JsonProcessingException {
        return ResponseEntity.ok(adverseEventService.updateStatus(aeId, status));
    }

    @PatchMapping("/{aeId}/severity")
    @PreAuthorize("hasAnyRole('PI','COMPLIANCE')")
    public ResponseEntity<AdverseEventResponseDto> updateSeverity(@PathVariable Long aeId,
                                                                  @RequestParam Severity severity) throws JsonProcessingException {
        return ResponseEntity.ok(adverseEventService.updateSeverity(aeId, severity));
    }

    @DeleteMapping("/{aeId}")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR','TECHNICIAN')")
    public ResponseEntity<String> deleteAE(@PathVariable Long aeId) throws JsonProcessingException {
        return ResponseEntity.ok(adverseEventService.deleteAE(aeId));
    }


    @GetMapping("/{aeId}/full")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public ResponseEntity<ApiResponseDto> getFullAdverseEvent(@PathVariable Long aeId) {
        return ResponseEntity.ok(adverseEventService.getFullAdverseEvent(aeId));
    }
}