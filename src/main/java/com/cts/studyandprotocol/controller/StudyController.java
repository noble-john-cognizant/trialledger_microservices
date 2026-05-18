package com.cts.studyandprotocol.controller;

import com.cts.studyandprotocol.dto.StudyRequestDto;
import com.cts.studyandprotocol.dto.StudyResponseDto;
import com.cts.studyandprotocol.model.StudyStatus;
import com.cts.studyandprotocol.service.StudyService;
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
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR','AUDITOR','COMPLIANCE','DATA_MANAGER')")
    public ResponseEntity<List<StudyResponseDto>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }

    @GetMapping("/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI','TECHNICIAN','AUDITOR','COMPLIANCE','DATA_MANAGER','COORDINATOR')")
    public ResponseEntity<StudyResponseDto> getStudyById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getStudyById(studyId));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PI','COORDINATOR')")
    public ResponseEntity<StudyResponseDto> createStudy(@Valid @RequestBody StudyRequestDto dto) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createStudy(dto));
    }

    @PatchMapping("/{studyId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','PI')")
    public ResponseEntity<String> updateStudyStatus(
            @PathVariable Long studyId,
            @RequestParam StudyStatus status) throws JsonProcessingException {
        return ResponseEntity.ok(studyService.updateStudyStatus(studyId, status));
    }

    @DeleteMapping("/{studyId}")
    @PreAuthorize("hasAnyRole('ADMIN','PI')")
    public ResponseEntity<String> deleteStudy(@PathVariable Long studyId) throws JsonProcessingException {
        return ResponseEntity.ok(studyService.deleteStudy(studyId));
    }
}
