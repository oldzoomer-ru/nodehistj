package ru.oldzoomer.nodehistj.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Standard error response DTO returned by all NodehistJ services.
 */
@Getter
@Builder
public class ErrorResponse {

    private final int status;
    private final String errorCode;
    private final String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    public static ErrorResponse of(int status, String errorCode, String message) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now(ZoneId.of("UTC")))
                .build();
    }
}