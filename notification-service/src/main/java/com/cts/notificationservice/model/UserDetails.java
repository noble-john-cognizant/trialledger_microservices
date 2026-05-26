package com.cts.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDetails {

    private Long userId;
    private String name;
    private String role;
    private String email;
}
