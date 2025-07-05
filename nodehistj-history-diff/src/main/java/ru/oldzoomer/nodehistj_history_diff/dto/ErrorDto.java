package ru.oldzoomer.nodehistj_history_diff.dto;

import java.time.LocalDateTime;

public record ErrorDto(
    String error,
    LocalDateTime timestamp,
    String path
) {
    public ErrorDto(String error) {
        this(error, LocalDateTime.now(), null);
    }
}