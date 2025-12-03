package ru.oldzoomer.nodehistj_newest_nodelists.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.oldzoomer.nodehistj_newest_nodelists.dto.ErrorDto;

import java.util.NoSuchElementException;

/**
 * Global exception handler for REST controllers.
 * This class provides centralized exception handling for REST controllers.
 * It handles various types of exceptions and returns appropriate error responses.
 */
@RestControllerAdvice
@Slf4j
public class ExceptionResolver {

    /**
     * Handles exceptions related to resources not found.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing an ErrorDto with the error details
     */
    @ExceptionHandler({NoSuchElementException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto("Not Found", ex.getMessage()));
    }

    /**
     * Handles bad request exceptions.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing an ErrorDto with the error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("Bad Request", ex.getMessage()));
    }

    /**
     * Handles validation exceptions.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing an ErrorDto with the error details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleValidationError(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("Validation Error", ex.getMessage()));
    }

    /**
     * Handles type mismatch exceptions.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing an ErrorDto with the error details
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(), ex.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDto("Type Mismatch", message));
    }

    /**
     * Handles internal server errors.
     *
     * @param ex the exception to handle
     * @return a ResponseEntity containing an ErrorDto with the error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleInternalError(Exception ex) {
        log.error("Internal server error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Internal Error", "Please try again later"));
    }
}