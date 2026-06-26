package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidVerificationCodeException extends ApiException {

    public InvalidVerificationCodeException() {
        super(
            "Invalid verification code",
            HttpStatus.BAD_REQUEST,
            "INVALID_VERIFICATION_CODE",
            "Please check your code and try again"
        );
    }
}
