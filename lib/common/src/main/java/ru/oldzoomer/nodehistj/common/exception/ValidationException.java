package ru.oldzoomer.nodehistj.common.exception;

/**
 * Exception thrown when request validation fails.
 * Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, 400, "VALIDATION_ERROR");
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, 400, "VALIDATION_ERROR");
    }
}
