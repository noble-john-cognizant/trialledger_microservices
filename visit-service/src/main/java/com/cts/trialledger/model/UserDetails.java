package com.cts.visit.model;


public class UserDetails {
    private Long userId;
    private String name;

    public Long getUserId() {
        return userId;
    }

    public UserDetails(Long userId, String name, String role, String email) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    private String role;
    private String email;
}
