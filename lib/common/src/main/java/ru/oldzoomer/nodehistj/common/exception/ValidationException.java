package ru.oldzoomer.nodehistj.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when request validation fails.
 * Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    }
}
