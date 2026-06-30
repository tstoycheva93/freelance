package com.thesis.freelance.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPostingDataException extends ApiException {
    
    public InvalidPostingDataException(String message) {
        super(
            message,
            HttpStatus.BAD_REQUEST,
            "INVALID_POSTING_DATA",
            "Please check your input data and try again"
        );
    }
}
