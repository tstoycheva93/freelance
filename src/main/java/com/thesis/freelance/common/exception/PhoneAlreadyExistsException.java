package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class PhoneAlreadyExistsException extends ApiException {
    
    public PhoneAlreadyExistsException(String phone) {
        super(
            "Phone already registered: " + phone,
            HttpStatus.CONFLICT,
            "PHONE_ALREADY_EXISTS",
            "Please use a different phone number"
        );
    }
}
