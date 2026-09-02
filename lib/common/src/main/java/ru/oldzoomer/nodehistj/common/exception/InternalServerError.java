package ru.oldzoomer.nodehistj.common.exception;

/**
 * Exception thrown when an unexpected internal error occurs.
 * Maps to HTTP 500 Internal Server Error.
 */
public class InternalServerError extends BusinessException {

    public InternalServerError(String message) {
        super(message, 500, "INTERNAL_ERROR");
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause, 500, "INTERNAL_ERROR");
    }
}
