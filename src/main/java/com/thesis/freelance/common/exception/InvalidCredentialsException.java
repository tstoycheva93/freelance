package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {
    
    public InvalidCredentialsException() {
        super(
            "Invalid username or password",
            HttpStatus.UNAUTHORIZED,
            "INVALID_CREDENTIALS",
            "Please check your credentials and try again"
        );
    }
}
