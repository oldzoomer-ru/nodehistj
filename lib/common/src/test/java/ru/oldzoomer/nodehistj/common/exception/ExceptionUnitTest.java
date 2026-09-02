package ru.oldzoomer.nodehistj.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for exception classes.
 */
class ExceptionUnitTest {

    @Test
    void businessExceptionShouldHaveCorrectStatus() {
        BusinessException ex = new ResourceNotFoundException("Test");
        assertEquals(404, ex.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode());
        assertEquals("Test", ex.getMessage());
    }

    @Test
    void resourceNotFoundExceptionShouldMapTo404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Node not found");
        assertEquals(404, ex.getStatus());
        assertEquals("RESOURCE_NOT_FOUND", ex.getErrorCode());
    }

    @Test
    void validationExceptionShouldMapTo400() {
        ValidationException ex = new ValidationException("Invalid input");
        assertEquals(400, ex.getStatus());
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
    }

    @Test
    void internalServerErrorShouldMapTo500() {
        InternalServerError ex = new InternalServerError("Something broke");
        assertEquals(500, ex.getStatus());
        assertEquals("INTERNAL_ERROR", ex.getErrorCode());
    }

    @Test
    void exceptionsShouldPreserveCause() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void exceptionWithCauseShouldHaveCorrectStatus() {
        Throwable cause = new RuntimeException("DB error");
        InternalServerError ex = new InternalServerError("Service unavailable", cause);
        assertEquals(500, ex.getStatus());
        assertEquals("INTERNAL_ERROR", ex.getErrorCode());
        assertSame(cause, ex.getCause());
    }
}
