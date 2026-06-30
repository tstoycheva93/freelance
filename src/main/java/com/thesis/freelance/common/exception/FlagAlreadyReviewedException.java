package com.thesis.freelance.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class FlagAlreadyReviewedException extends ApiException {

    public FlagAlreadyReviewedException(UUID id) {
        super(
            "Flag has already been reviewed: " + id,
            HttpStatus.CONFLICT,
            "FLAG_ALREADY_REVIEWED",
            "This flag has already been reviewed and cannot be modified"
        );
    }
}
