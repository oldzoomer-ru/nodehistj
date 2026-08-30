package ru.oldzoomer.nodehistj.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when an unexpected internal error occurs.
 * Maps to HTTP 500 Internal Server Error.
 */
public class InternalServerError extends BusinessException {

    public InternalServerError(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }
}
