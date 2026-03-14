package com.fudgeq.api.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Input values are invalid", errors);
    }

    // 2. Business Logic Exceptions (e.g., UserAlreadyExists, Insufficient Stock)
    @ExceptionHandler({UserAlreadyExistsException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> handleBusinessLogic(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Business Logic Conflict", ex.getMessage(), null);
    }

    // 3. Security: Authentication Failures
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication failed: " + ex.getMessage(), null);
    }

    // 4. Security: Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to access this resource.", null);
    }

    // 5. Resource Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), null);
    }

    // 6. Invalid Enum Values or Bad Logic (e.g., Invalid OrderStatus)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "Invalid input or parameter: " + ex.getMessage(), null);
    }

    // 7. ID Generation or Database Lock Errors (From CustomIdGenerator)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {
        log.error("Runtime Exception: ", ex); // Log the stack trace for internal debugging
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Request Error", ex.getMessage(), null);
    }

    // 8. General Catch-All
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected Error: ", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "An unexpected error occurred. Please contact support.", null);
    }

    /**
     * Helper method to maintain consistent response structure
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String error, String message, Map<String, String> validationErrors) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();
        return new ResponseEntity<>(response, status);
    }
}
