package com.cts.adverseevent.controller;

import com.cts.adverseevent.dto.AdverseEventRequestDto;
import com.cts.adverseevent.dto.AdverseEventResponseDto;
import com.cts.adverseevent.dto.ApiResponseDto;
import com.cts.adverseevent.service.AdverseEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<AdverseEventResponseDto>> getAllAE() {
        return ResponseEntity.ok(adverseEventService.getAllAE());
    }

    @GetMapping("/{aeId}")
    public ResponseEntity<AdverseEventResponseDto> getAEById(@PathVariable Long aeId) {
        return ResponseEntity.ok(adverseEventService.getAEById(aeId));
    }

    @GetMapping("/study/{studyId}")
    public ResponseEntity<List<AdverseEventResponseDto>> getByStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(adverseEventService.getAEByStudy(studyId));
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<AdverseEventResponseDto>> getByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(adverseEventService.getAEByParticipant(participantId));
    }

    @PostMapping
    public ResponseEntity<AdverseEventResponseDto> createAE(@Valid @RequestBody AdverseEventRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adverseEventService.createAE(dto));
    }

    @PatchMapping("/{aeId}/status")
    public ResponseEntity<AdverseEventResponseDto> updateStatus(@PathVariable Long aeId,
                                                                @RequestParam String status) {
        return ResponseEntity.ok(adverseEventService.updateStatus(aeId, status));
    }

    @DeleteMapping("/{aeId}")
    public ResponseEntity<String> deleteAE(@PathVariable Long aeId) {
        return ResponseEntity.ok(adverseEventService.deleteAE(aeId));
    }

    /**
     * Aggregation endpoint: returns AE + follow-ups + study (Feign) + participant (Feign).
     * If a downstream service is unavailable, that section returns null
     * instead of failing the whole request.
     */
    @GetMapping("/{aeId}/full")
    public ResponseEntity<ApiResponseDto> getFullAdverseEvent(@PathVariable Long aeId) {
        return ResponseEntity.ok(adverseEventService.getFullAdverseEvent(aeId));
    }
}
