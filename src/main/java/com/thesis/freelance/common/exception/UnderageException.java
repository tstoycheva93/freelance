package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class UnderageException extends ApiException {
    
    public UnderageException(int age) {
        super(
            "User must be at least 16 years old. Current age: " + age,
            HttpStatus.BAD_REQUEST,
            "UNDERAGE_USER",
            "You must be 16 or older to register on this platform"
        );
    }
}
