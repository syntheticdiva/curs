package com.smp.curs.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyNotFoundExceptionTest {

    @Test
    void testExceptionCreation() {
        // Arrange
        String errorMessage = "Курс японской йены (JPY) не найден";

        // Act
        CurrencyNotFoundException exception = new CurrencyNotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testResponseStatusAnnotation() {
        // Act
        ResponseStatus responseStatus = CurrencyNotFoundException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
    }

    @Test
    void testExceptionWithYenMessages() {
        // Arrange
        String[] yenMessages = {
                "Курс йены JPY не найден",
                "Данные по йене отсутствуют",
                "Валютный курс йены не определен"
        };

        // Act & Assert
        for (String message : yenMessages) {
            CurrencyNotFoundException exception = new CurrencyNotFoundException(message);
            assertEquals(message, exception.getMessage());
        }
    }

    @Test
    void testYenExceptionDetails() {
        // Arrange
        String specificYenMessage = "Курс японской йены (JPY) не может быть загружен";

        // Act
        CurrencyNotFoundException exception = new CurrencyNotFoundException(specificYenMessage);

        // Assert
        assertTrue(exception.getMessage().contains("JPY"));
        assertTrue(exception.getMessage().contains("йены"));
    }
}