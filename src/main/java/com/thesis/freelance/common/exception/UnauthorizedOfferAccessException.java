package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedOfferAccessException extends RuntimeException {
    public UnauthorizedOfferAccessException() {
        super("You are not authorized to delete this offer");
    }
}
