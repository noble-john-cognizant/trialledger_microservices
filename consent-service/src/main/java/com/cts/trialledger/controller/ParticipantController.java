package com.cts.trialledger.controller;


import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.Participant;
import com.cts.trialledger.model.EnrollmentStatus;
import com.cts.trialledger.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('PI','COORDINATOR')")
    public ParticipantResponseDTO create(@RequestBody ParticipantRequestDTO dto) {
        return service.createParticipant(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER')")
    public List<ParticipantResponseDTO> getAll() {
        return service.getParticipants();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','TECHNICIAN','DATA_MANAGER','AUDITOR','PARTICIPANT')")
    public ParticipantResponseDTO getById(@PathVariable Long id) {
               return service.getParticipantById(id);

    }

    /**
     * Look up a participant by phone number. Available to the participant
     * themselves so they can auto-resolve their enrollment after login.
     */
    @GetMapping("/by-phone/{phone}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','TECHNICIAN','DATA_MANAGER','AUDITOR','PARTICIPANT')")
    public ParticipantResponseDTO getByPhone(@PathVariable String phone) {
        return service.getParticipantByPhone(phone);
    }

    @GetMapping("/study/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','DATA_MANAGER','AUDITOR')")
    public List<Participant> getByStudyId(@PathVariable Long studyId){
        return service.getByStudyId(studyId);
    }

    @GetMapping("/stats/{studyId}")
    public EnrollmentStatsDTO getEnrollmentStats(@PathVariable Long studyId) {
        return service.getEnrollmentStatus(studyId);
    }

    @PatchMapping("/{participantId}/status")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR')")
    public ParticipantResponseDTO updateEnrollmentStatus(
            @PathVariable Long participantId,
            @RequestParam EnrollmentStatus status) {

        ParticipantResponseDTO response = service.updateEnrollmentStatus(participantId, status);
        return response;
    }

    @DeleteMapping("/{participantId}")
    @PreAuthorize("hasAnyRole('COORDINATOR','PARTICIPANT')")
    public ParticipantResponseDTO cancelParticipant(
            @PathVariable Long participantId) {

        return service.cancelParticipant(participantId);
    }
}