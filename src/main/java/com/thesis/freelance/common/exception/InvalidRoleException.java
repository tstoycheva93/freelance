package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidRoleException extends ApiException {
    
    public InvalidRoleException(String roleName) {
        super(
            "Invalid role: " + roleName,
            HttpStatus.BAD_REQUEST,
            "INVALID_ROLE",
            "Please select a valid role (Client or Freelancer)"
        );
    }
}
