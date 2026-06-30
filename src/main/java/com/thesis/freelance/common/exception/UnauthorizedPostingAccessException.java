package com.thesis.freelance.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class UnauthorizedPostingAccessException extends ApiException {
    
    public UnauthorizedPostingAccessException(UUID postingId) {
        super(
            "You are not authorized to access posting: " + postingId,
            HttpStatus.FORBIDDEN,
            "UNAUTHORIZED_POSTING_ACCESS",
            "You can only access your own postings"
        );
    }
}
