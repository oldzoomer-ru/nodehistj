package ru.oldzoomer.nodehistj.common.exception;

import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

/**
 * Global exception handler for all NodehistJ REST services.
 * Catches all exceptions and converts them to consistent JSON error responses.
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles business exceptions (ResourceNotFoundException, ValidationException, etc.)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Handles missing required request parameters (overrides base class to return custom JSON).
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        ErrorResponse error = ErrorResponse.of(
                400,
                "VALIDATION_ERROR",
                "Missing required parameter: " + ex.getParameterName()
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles type mismatch for query/path parameters (e.g., year=abc instead of year=2024)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Type mismatch for parameter {}: value='{}'", ex.getName(), ex.getValue());
        ErrorResponse error = ErrorResponse.of(
                400,
                "VALIDATION_ERROR",
                "Invalid value for parameter '" + ex.getName() + "': '" + ex.getValue() + "'"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles @Valid annotation failures on request body
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", errors);
        ErrorResponse error = ErrorResponse.of(
                400,
                "VALIDATION_ERROR",
                "Validation failed: " + errors
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles illegal state exceptions (e.g., resource already exists, invalid state transition)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                409,
                "CONFLICT",
                ex.getMessage()
        );
        return ResponseEntity.status(409).body(error);
    }

    /**
     * Handles all uncaught exceptions as 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorResponse error = ErrorResponse.of(
                500,
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
        );
        return ResponseEntity.internalServerError().body(error);
    }
}
