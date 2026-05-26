package com.cts.notificationservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyDTO {

    private Long studyId;
    private String title;
    private String sponsor;
    private String protocolNumber;
    private String status;
}
