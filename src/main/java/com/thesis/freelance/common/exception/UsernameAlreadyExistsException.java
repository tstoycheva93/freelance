package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ApiException {
    
    public UsernameAlreadyExistsException(String username) {
        super(
            "Username already taken: " + username,
            HttpStatus.CONFLICT,
            "USERNAME_ALREADY_EXISTS",
            "Please choose a different username"
        );
    }
}
