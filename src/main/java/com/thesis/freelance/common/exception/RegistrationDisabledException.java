package com.thesis.freelance.common.exception;

public class RegistrationDisabledException extends RuntimeException {
    public RegistrationDisabledException(String message) {
        super(message != null && !message.isBlank() ? message : "Registration is currently disabled");
    }
}
