package com.thesis.freelance.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.LinkedHashMap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private boolean isProduction() {
        return "prod".equals(activeProfile) || "production".equals(activeProfile);
    }

    private Map<String, Object> createErrorResponse(
            HttpStatus status,
            String errorCode,
            String message,
            String suggestion,
            HttpServletRequest request,
            Exception ex) {
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("code", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("suggestion", suggestion);
        errorResponse.put("path", request.getRequestURI());
        
        // Include stack trace only in non-production environments
        if (!isProduction()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            errorResponse.put("stackTrace", sw.toString());
        }
        
        return errorResponse;
    }

    // Handle custom API exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(
            ApiException ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            ex.getStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            ex.getSuggestion(),
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    // Handle account disabled exception specifically
    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDisabledException(
            AccountDisabledException ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.FORBIDDEN,
            "ACCOUNT_DISABLED",
            ex.getMessage(),
            "Please contact support to reactivate your account",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle phone already exists exception
    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handlePhoneAlreadyExistsException(
            PhoneAlreadyExistsException ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.CONFLICT,
            "PHONE_ALREADY_EXISTS",
            ex.getMessage(),
            "Please use a different phone number",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("path", request.getRequestURI());
        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    // Handle InternalAuthenticationServiceException (Spring Security wrapper)
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Map<String, Object>> handleInternalAuthException(
            InternalAuthenticationServiceException ex, HttpServletRequest request) {
        
        // Check if the cause is AccountDisabledException
        if (ex.getCause() instanceof AccountDisabledException) {
            AccountDisabledException cause = (AccountDisabledException) ex.getCause();
            Map<String, Object> errorResponse = createErrorResponse(
                HttpStatus.FORBIDDEN,
                "ACCOUNT_DISABLED",
                cause.getMessage(),
                "Please contact support to reactivate your account",
                request,
                ex
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        
        // Otherwise treat as generic authentication failure
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "AUTHENTICATION_FAILED",
            "Authentication failed: " + ex.getMessage(),
            "Please check your credentials and try again",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Handle authentication exceptions
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            Exception ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "AUTHENTICATION_FAILED",
            "Invalid username or password",
            "Please check your credentials and try again",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Handle validation exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            ex.getMessage(),
            "Please check your input and try again",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception ex, HttpServletRequest request) {
        
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred",
            "Please try again later or contact support if the problem persists",
            request,
            ex
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
