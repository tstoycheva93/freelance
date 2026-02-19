package com.thesis.freelance.common.exception;

public class AccountDisabledException extends RuntimeException {
    
    public AccountDisabledException(String msg) {
        super(msg);
    }
    
    public AccountDisabledException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
