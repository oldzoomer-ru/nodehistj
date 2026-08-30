package ru.oldzoomer.nodehistj.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all business exceptions in NodehistJ services.
 * Provides HTTP status and error code for consistent API error responses.
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected BusinessException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    protected BusinessException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
