package com.cts.trialledger.controller;

import com.cts.trialledger.dto.*;
import com.cts.trialledger.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService service;

    //  CREATE
    @PostMapping
    @PreAuthorize("hasAnyRole('COORDINATOR','PARTICIPANT')")
    public ConsentResponseDTO create(@RequestBody ConsentRequestDTO dto) throws Exception {
        return service.createConsent(dto);
    }

    //  GET BY PARTICIPANT
    @GetMapping("/participant/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','TECHNICIAN','COMPLIANCE','PARTICIPANT','AUDITOR')")
    public List<ConsentResponseDTO> getByParticipant(@PathVariable Long id) {
        return service.getConsentsByParticipant(id);
    }

    //  WITHDRAW
    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('PI','COORDINATOR','PARTICIPANT')")
    public String withdraw(@PathVariable Long id,
                           @RequestBody ConsentWithdrawalDTO dto) {

        dto.setConsentId(id);
        return service.withdrawConsent(dto);
    }

    //  VERIFY API (NEW)
    @GetMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','AUDITOR')")
    public String verify(@PathVariable Long id) throws Exception {
        return service.verifyConsent(id);
    }

    @GetMapping("/study/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','COMPLIANCE','AUDITOR')")
    public List<ConsentResponseDTO>
    getByStudy(@PathVariable Long studyId){
        return service.getConsentsByStudyId(studyId);
    }

}

