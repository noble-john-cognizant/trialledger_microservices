package com.cts.studyandprotocol.dto;

import com.cts.studyandprotocol.model.ProtocolStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolVersionResponseDto {
    private Long protocolId;
    private Long studyId;
    private String versionNumber;
    private String documentUrl;
    private LocalDate effectiveDate;
    private Long approvedById;
    private ProtocolStatus status;
    private Boolean isDeleted;
}