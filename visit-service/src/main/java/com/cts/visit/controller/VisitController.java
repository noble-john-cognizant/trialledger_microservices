package com.cts.visit.controller;

import com.cts.visit.api.ApiResponseDto;
import com.cts.visit.dto.VisitRequestDto;
import com.cts.visit.dto.VisitResponseDto;
import com.cts.visit.enums.VisitStatus;
import com.cts.visit.service.VisitService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    // 1. Schedule a new visit
    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR')")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> scheduleVisit(
            @Valid @RequestBody VisitRequestDto visitRequestDto) throws JsonProcessingException {

        VisitResponseDto response = visitService.scheduleVisit(visitRequestDto);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Visit scheduled successfully", response)
        );
    }

    // 2. Get all visits for a participant
    @GetMapping("/participant/{participantId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','PARTICIPANT')")
    public ResponseEntity<ApiResponseDto<List<VisitResponseDto>>> getVisitsByParticipant(
            @PathVariable Long participantId) {

        List<VisitResponseDto> response = visitService.getVisitsByParticipant(participantId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Visits fetched successfully", response)
        );
    }

    // 3. Update visit status
    @PutMapping("/{visitId}/status")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR')")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> updateVisitStatus(
            @PathVariable Long visitId,
            @RequestParam VisitStatus status) throws JsonProcessingException {

        VisitResponseDto response = visitService.updateVisitStatus(visitId, status);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Visit status updated successfully", response)
        );
    }

    // 4. Get visit by ID
    @GetMapping("/{visitId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','PARTICIPANT')")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> getVisitById(
            @PathVariable Long visitId) throws JsonProcessingException {

        VisitResponseDto response = visitService.getVisitById(visitId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Visit fetched successfully", response)
        );
    }

    // 5. Cancel visit by ID (soft delete)
    @DeleteMapping("/{visitId}")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR','PARTICIPANT')")
    public ResponseEntity<ApiResponseDto<VisitResponseDto>> cancelVisit(
            @PathVariable Long visitId) throws JsonProcessingException {

        VisitResponseDto response = visitService.cancelVisit(visitId);

        return ResponseEntity.ok(
                new ApiResponseDto<>("SUCCESS", "Visit cancelled successfully", response)
        );
    }

     // 6. Get all visits for a study
     @GetMapping("/study/{studyId}")
     @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','PARTICIPANT')")
     public ResponseEntity<ApiResponseDto<List<VisitResponseDto>>> getVisitsByStudy(
             @PathVariable Long studyId) {

         List<VisitResponseDto> response =
                 visitService.getVisitsByStudy(studyId);

         return ResponseEntity.ok(
                 new ApiResponseDto<>("SUCCESS", "Visits for study fetched successfully", response)
         );
     }

}
