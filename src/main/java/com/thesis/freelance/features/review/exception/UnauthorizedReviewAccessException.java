package com.thesis.freelance.features.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedReviewAccessException extends RuntimeException {
    public UnauthorizedReviewAccessException() {
        super("You are not authorized to delete this review");
    }
}
