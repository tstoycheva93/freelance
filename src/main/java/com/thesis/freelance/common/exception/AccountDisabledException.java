package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends ApiException {
    
    public AccountDisabledException() {
        super(
            "Your account has been deactivated",
            HttpStatus.FORBIDDEN,
            "ACCOUNT_DISABLED",
            "Please contact support to reactivate your account"
        );
    }
}
