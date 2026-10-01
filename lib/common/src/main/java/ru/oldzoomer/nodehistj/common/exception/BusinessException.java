package ru.oldzoomer.nodehistj.common.exception;

import lombok.Getter;

/**
 * Base class for all business exceptions in NodehistJ services.
 * Provides HTTP status and error code for consistent API error responses.
 */
@Getter
public abstract class BusinessException extends RuntimeException {

    private final int status;
    private final String errorCode;

    protected BusinessException(String message, int status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    protected BusinessException(String message, Throwable cause, int status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}
