package ru.oldzoomer.common.utils;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit-тесты для ExceptionResolver без поднятия Spring-контекста.
 * Проверяются коды ответа, заполнение ErrorDto и граничные случаи сообщений.
 */
class ExceptionResolverTest {

    private final ExceptionResolver resolver = new ExceptionResolver();

    @SuppressWarnings("null")
    @Test
    @DisplayName("NoSuchElementException -> 404 Not Found и корректный ErrorDto")
    void handleNotFoundReturns404() {
        // Arrange
        NoSuchElementException ex = new NoSuchElementException("entity not found");

        // Act
        ResponseEntity<ErrorDto> resp = resolver.handleNotFound(ex);

        // Assert
        assertEquals(404, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("Not Found", resp.getBody().getMessage());
        assertEquals("entity not found", resp.getBody().getCode());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("IllegalArgumentException -> 400 Bad Request и корректный ErrorDto")
    void handleBadRequestReturns400() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");

        // Act
        ResponseEntity<ErrorDto> resp = resolver.handleBadRequest(ex);

        // Assert
        assertEquals(400, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("Bad Request", resp.getBody().getMessage());
        assertEquals("bad arg", resp.getBody().getCode());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("ConstraintViolationException -> 400 Validation Error и проксирует message")
    void handleValidationErrorReturns400() {
        // Arrange
        ConstraintViolationException ex = new ConstraintViolationException("invalid", null);

        // Act
        ResponseEntity<ErrorDto> resp = resolver.handleValidationError(ex);

        // Assert
        assertEquals(400, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("Validation Error", resp.getBody().getMessage());
        assertEquals("invalid", resp.getBody().getCode());
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("MethodArgumentTypeMismatchException -> 400 Type Mismatch и форматированное сообщение")
    void handleTypeMismatchReturns400() {
        // Arrange
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "ABC", String.class, "year", null, new IllegalArgumentException("type")
        );

        // Act
        ResponseEntity<ErrorDto> resp = resolver.handleTypeMismatch(ex);

        // Assert
        assertEquals(400, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("Type Mismatch", resp.getBody().getMessage());
        // "Invalid value 'ABC' for parameter 'year'"
        org.assertj.core.api.Assertions.assertThat(resp.getBody().getCode())
                .contains("Invalid value 'ABC' for parameter 'year'");
    }

    @SuppressWarnings("null")
    @Test
    @DisplayName("Exception -> 500 Internal Error и безопасное сообщение")
    void handleInternalErrorReturns500() {
        // Arrange
        Exception ex = new Exception("internal reason");

        // Act
        ResponseEntity<ErrorDto> resp = resolver.handleInternalError(ex);

        // Assert
        assertEquals(500, resp.getStatusCode().value());
        Assertions.assertNotNull(resp.getBody());
        assertEquals("Internal Error", resp.getBody().getMessage());
        assertEquals("Please try again later", resp.getBody().getCode());
    }
}