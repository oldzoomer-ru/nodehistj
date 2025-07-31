package ru.oldzoomer.common.utils;

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
    private String message;
    private String code;
    private Instant timestamp = Instant.now();

    public ErrorDto(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public ErrorDto(String message) {
        this.message = message;
        this.code = "GENERIC_ERROR";
    }
}