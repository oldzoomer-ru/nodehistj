package ru.gavrilovegor519.nodehistj_historic_nodelists.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.gavrilovegor519.nodehistj_historic_nodelists.dto.ErrorDto;

@RestControllerAdvice
@Log4j2
public class ExceptionResolver {
    /**
     * Handle illegal argument exception
     * @param ex exception
     * @return response entity with error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception", ex);
        return ResponseEntity.badRequest().body(new ErrorDto("You have entered an illegal argument"));
    }

    /**
     * Handle not found exception
     * @return response entity with error message
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NoResourceFoundException ex) {
        log.error("Not found exception", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        log.error("Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Problem with server. Please try again later."));
    }
}
