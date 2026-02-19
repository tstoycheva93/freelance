package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends ApiException {
    
    public EmailAlreadyExistsException(String email) {
        super(
            "Email already registered: " + email,
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS",
            "Please use a different email or try logging in"
        );
    }
}
