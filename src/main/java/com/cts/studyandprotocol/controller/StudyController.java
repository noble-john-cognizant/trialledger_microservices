package com.cts.studyandprotocol.controller;

import com.cts.studyandprotocol.dto.StudyRequestDto;
import com.cts.studyandprotocol.dto.StudyResponseDto;
import com.cts.studyandprotocol.service.StudyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping
    public ResponseEntity<List<StudyResponseDto>> getAllStudies() {
        return ResponseEntity.ok(studyService.getAllStudies());
    }

    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponseDto> getStudyById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.getStudyById(studyId));
    }

    @PostMapping
    public ResponseEntity<StudyResponseDto> createStudy(@Valid @RequestBody StudyRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studyService.createStudy(dto));
    }

    @DeleteMapping("/{studyId}")
    public ResponseEntity<String> deleteStudy(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.deleteStudy(studyId));
    }
}