package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflict(ConflictException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "Operation violates data integrity constraints", request, null);
    }

    @ExceptionHandler({ForbiddenOperationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> forbidden(RuntimeException ex, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    @ExceptionHandler({IllegalArgumentException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return error(HttpStatus.BAD_REQUEST, "Validation failed", request, validationErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internal(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request, null);
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status,
                                                String message,
                                                HttpServletRequest request,
                                                Map<String, String> validationErrors) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors
        ));
    }
}
