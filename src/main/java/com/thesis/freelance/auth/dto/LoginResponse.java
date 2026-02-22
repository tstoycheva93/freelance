package com.thesis.freelance.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private String message;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String photoUrl;
    private UUID userId;
}
