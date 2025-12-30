package ru.oldzoomer.nodehistj_newest_nodelists.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

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
    /**
     * Error message.
     */
    private final String message;

    /**
     * Error code.
     */
    private final String code;

    /**
     * Timestamp of the error.
     */
    private final Instant timestamp = Instant.now();

    /**
     * Constructs a new ErrorDto with the specified message and code.
     *
     * @param message the error message
     * @param code    the error code
     */
    public ErrorDto(String message, String code) {
        this.message = message;
        this.code = code;
    }
}