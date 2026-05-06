package com.cts.trialledger.dto;


import java.util.List;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponseDTO {

    private SampleResponseDTO sample;
    private List<ChainOfCustodyResponseDTO> chainOfCustody;
    private List<SampleStorageResponseDTO> storageHistory;
    private List<AssayRunResponseDTO> assayRuns;
    private StudyResponseDTO study;
    private ParticipantResponseDTO participant;

    public ApiResponseDTO(SampleResponseDTO sample,
                                List<ChainOfCustodyResponseDTO> chainOfCustody,
                                List<SampleStorageResponseDTO> storageHistory,
                                List<AssayRunResponseDTO> assayRuns,
                                StudyResponseDTO study,
                                ParticipantResponseDTO participant) {

        this.sample = sample;
        this.chainOfCustody = chainOfCustody;
        this.storageHistory = storageHistory;
        this.assayRuns = assayRuns;
        this.study = study;
        this.participant = participant;
    }
}