package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.ErrorDto;

@RestControllerAdvice
public class ExceptionResolver {
    /**
     * Handle illegal argument exception
     * @param ex exception
     * @return response entity with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    /**
     * Handle not found exception
     * @return response entity with error message
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
