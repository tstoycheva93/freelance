package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class RoleNotFoundException extends ApiException {
    
    public RoleNotFoundException(UUID roleId) {
        super(
            "Role not found with ID: " + roleId,
            HttpStatus.NOT_FOUND,
            "ROLE_NOT_FOUND",
            "Please select a valid role"
        );
    }
}
