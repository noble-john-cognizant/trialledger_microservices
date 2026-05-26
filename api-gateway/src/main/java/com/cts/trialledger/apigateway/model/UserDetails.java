package com.cts.trialledger.apigateway.model;

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
