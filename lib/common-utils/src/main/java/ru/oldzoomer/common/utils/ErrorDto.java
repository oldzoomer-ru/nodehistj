package ru.oldzoomer.common.utils;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified error response DTO.
 * Contains:
 * - Error message
 * - Error code
 * - Timestamp
 * - Optional details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {
    private String message;
    private String code;
    private Instant timestamp = Instant.now();
    private Object details;

    public ErrorDto(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public ErrorDto(String message) {
        this.message = message;
        this.code = "GENERIC_ERROR";
    }
}