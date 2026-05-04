package com.cts.trialledger.controller;

import com.cts.trialledger.dto.*;
import com.cts.trialledger.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService service;

    //  CREATE
    @PostMapping
    public ConsentResponseDTO create(@RequestBody ConsentRequestDTO dto) throws Exception {
        return service.createConsent(dto);
    }

    //  GET BY PARTICIPANT
    @GetMapping("/participant/{id}")
    public List<ConsentResponseDTO> getByParticipant(@PathVariable Long id) {
        return service.getConsentsByParticipant(id);
    }

    //  WITHDRAW
    @PostMapping("/{id}/withdraw")
    public String withdraw(@PathVariable Long id,
                           @RequestBody ConsentWithdrawalDTO dto) {

        dto.setConsentId(id);
        return service.withdrawConsent(dto);
    }

    //  VERIFY API (NEW)
    @GetMapping("/{id}/verify")
    public String verify(@PathVariable Long id) throws Exception {
        return service.verifyConsent(id);
    }

    @GetMapping("/study/{studyId}")
    public List<ConsentResponseDTO>
    getByStudy(@PathVariable Long studyId){
        return service.getConsentsByStudyId(studyId);
    }

}

