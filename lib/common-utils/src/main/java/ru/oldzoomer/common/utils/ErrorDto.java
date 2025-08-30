package ru.oldzoomer.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

/**
 * Unified error response DTO.
 * Contains:
 * - Error message
 * - Error code
 * - Timestamp
 * - Optional details
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ErrorDto {
    private final String message;
    private final String code;
    private final Instant timestamp = Instant.now();

    /**
     * Constructor for ErrorDto with message and code.
     *
     * @param message Error message
     * @param code Error code
     */
    public ErrorDto(String message, String code) {
        this.message = message;
        this.code = code;
    }

    /**
     * Constructor for ErrorDto with message only.
     *
     * @param message Error message
     */
    public ErrorDto(String message) {
        this.message = message;
        this.code = "GENERIC_ERROR";
    }
}