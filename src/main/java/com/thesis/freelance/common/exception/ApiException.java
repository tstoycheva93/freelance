package com.thesis.freelance.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    private final String suggestion;
    
    protected ApiException(String message, HttpStatus status, String errorCode, String suggestion) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.suggestion = suggestion;
    }
    
    protected ApiException(String message, Throwable cause, HttpStatus status, String errorCode, String suggestion) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
        this.suggestion = suggestion;
    }
}
