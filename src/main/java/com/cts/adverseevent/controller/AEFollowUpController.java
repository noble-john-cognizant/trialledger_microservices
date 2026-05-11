package com.cts.adverseevent.controller;

import com.cts.adverseevent.dto.AEFollowUpRequestDto;
import com.cts.adverseevent.dto.AEFollowUpResponseDto;
import com.cts.adverseevent.service.AEFollowUpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adverse-events")
public class AEFollowUpController {

    private final AEFollowUpService followUpService;

    public AEFollowUpController(AEFollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @PostMapping("/{aeId}/follow-ups")
    public ResponseEntity<AEFollowUpResponseDto> addFollowUp(@PathVariable Long aeId,
                                                             @Valid @RequestBody AEFollowUpRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(followUpService.addFollowUp(aeId, dto));
    }

    @GetMapping("/{aeId}/follow-ups")
    public ResponseEntity<List<AEFollowUpResponseDto>> getFollowUps(@PathVariable Long aeId) {
        return ResponseEntity.ok(followUpService.getFollowUps(aeId));
    }

    @DeleteMapping("/follow-ups/{followUpId}")
    public ResponseEntity<String> deleteFollowUp(@PathVariable Long followUpId) {
        return ResponseEntity.ok(followUpService.deleteFollowUp(followUpId));
    }
}