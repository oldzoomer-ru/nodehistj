package ru.gavrilovegor519.nodehistj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.gavrilovegor519.nodehistj.dto.ErrorDto;

@RestControllerAdvice
public class ExceptionResolver {
    /**
     * Handle illegal argument exception
     * @param ex exception
     * @return response entity with error message
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
