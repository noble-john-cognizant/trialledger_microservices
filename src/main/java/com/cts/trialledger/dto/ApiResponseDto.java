package com.cts.trialledger.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ApiResponseDto {

    private AdverseEventResponseDto adverseEvent;
    private List<AEFollowUpResponseDto> followUps;
    private StudyDto study;
    private ParticipantDto participant;

    public ApiResponseDto(AdverseEventResponseDto adverseEvent,
                          List<AEFollowUpResponseDto> followUps,
                          StudyDto study,
                          ParticipantDto participant) {
        this.adverseEvent = adverseEvent;
        this.followUps = followUps;
        this.study = study;
        this.participant = participant;
    }
}
