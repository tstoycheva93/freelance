package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class FlagNotFoundException extends ApiException {

    public FlagNotFoundException(UUID id) {
        super(
            "Flag not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "FLAG_NOT_FOUND",
            "The flag you are looking for does not exist or has been removed"
        );
    }
}
