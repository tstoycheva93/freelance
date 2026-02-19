package com.thesis.freelance.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}
