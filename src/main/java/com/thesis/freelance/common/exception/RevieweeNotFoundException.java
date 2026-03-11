package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class RevieweeNotFoundException extends ApiException {
    
    public RevieweeNotFoundException() {
        super(
            "User to review not found",
            HttpStatus.NOT_FOUND,
            "REVIEWEE_NOT_FOUND",
            "Please check the user ID"
        );
    }
}
