package com.cts.trialledger.controller;

import com.cts.trialledger.dto.*;
import com.cts.trialledger.entity.Participant;
import com.cts.trialledger.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService service;

    @PostMapping
    public ParticipantResponseDTO create(@RequestBody ParticipantRequestDTO dto) {
        return service.createParticipant(dto);
    }

    @GetMapping
    public List<ParticipantResponseDTO> getAll() {
        return service.getParticipants();
    }

    @GetMapping("/{id}")
    public ParticipantResponseDTO getById(@PathVariable Long id) {
        return service.getParticipantById(id);
    }

    @GetMapping("/study/{studyId}")
    public List<Participant> getByStudyId(@PathVariable Long studyId){
        return service.getByStudyId(studyId);
    }
}

