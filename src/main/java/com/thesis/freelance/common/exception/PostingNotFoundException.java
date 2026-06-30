package com.thesis.freelance.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
public class PostingNotFoundException extends ApiException {
    
    public PostingNotFoundException(UUID id) {
        super(
            "Posting not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "POSTING_NOT_FOUND",
            "The posting you are looking for does not exist or has been removed"
        );
    }
}
