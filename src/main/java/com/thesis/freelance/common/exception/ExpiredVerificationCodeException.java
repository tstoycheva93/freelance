package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class ExpiredVerificationCodeException extends ApiException {

    public ExpiredVerificationCodeException() {
        super(
            "Verification code has expired",
            HttpStatus.BAD_REQUEST,
            "EXPIRED_VERIFICATION_CODE",
            "Please request a new verification code"
        );
    }
}
