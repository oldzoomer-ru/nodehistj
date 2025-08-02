package ru.oldzoomer.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * AAA-структура, детерминированные проверки, покрытие веток конструкторов.
 */
class ErrorDtoTest {

    @Test
    @DisplayName("ErrorDto(String, String): устанавливает message, code и timestamp (≈ now)")
    void ctorWithMessageAndCodeSetsFields() {
        // Arrange
        String msg = "Not Found";
        String code = "404";

        // Act
        ErrorDto dto = new ErrorDto(msg, code);

        // Assert
        assertEquals(msg, dto.getMessage());
        assertEquals(code, dto.getCode());
        assertNotNull(dto.getTimestamp());
        // timestamp должен быть около now
        assertTrue(Duration.between(dto.getTimestamp(), Instant.now()).abs().getSeconds() < 5);
    }

    @Test
    @DisplayName("ErrorDto(String): устанавливает message и GENERIC_ERROR как code")
    void ctorWithMessageSetsDefaultCode() {
        // Arrange
        String msg = "Any";

        // Act
        ErrorDto dto = new ErrorDto(msg);

        // Assert
        assertEquals(msg, dto.getMessage());
        assertEquals("GENERIC_ERROR", dto.getCode());
        assertNotNull(dto.getTimestamp());
    }
}