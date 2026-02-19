package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    
    public UserNotFoundException(String username) {
        super(
            "User not found with username: " + username,
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "Please check the username or register a new account"
        );
    }
}
