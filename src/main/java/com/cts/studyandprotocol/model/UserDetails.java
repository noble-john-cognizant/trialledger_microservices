package com.cts.studyandprotocol.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetails {
    private Long userId;
    private String name;
    private String role;
    private String email;
}
