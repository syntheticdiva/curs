package com.smp.curs.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceExceptionTest {

    @Test
    void testExceptionCreation() {
        // Arrange
        String errorMessage = "Ошибка при получении курса валют";
        Throwable cause = new RuntimeException("Сетевая ошибка");

        // Act
        CurrencyServiceException exception = new CurrencyServiceException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testResponseStatusAnnotation() {
        // Arrange & Act
        ResponseStatus responseStatus = CurrencyServiceException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatus.value());
    }

    @Test
    void testExceptionInheritance() {
        // Assert
        assertTrue(RuntimeException.class.isAssignableFrom(CurrencyServiceException.class));
    }

    @Test
    void testExceptionWithOnlyCause() {
        // Arrange
        Throwable cause = new RuntimeException("Первопричина ошибки");

        // Act
        CurrencyServiceException exception = new CurrencyServiceException("Общая ошибка сервиса", cause);

        // Assert
        assertEquals("Общая ошибка сервиса", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionChaining() {
        // Arrange
        Throwable rootCause = new IllegalArgumentException("Некорректный аргумент");
        Throwable intermediateCause = new RuntimeException("Промежуточная ошибка", rootCause);

        // Act
        CurrencyServiceException exception = new CurrencyServiceException("Составная ошибка", intermediateCause);

        // Assert
        assertEquals("Составная ошибка", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    void testSerializability() {
        // Arrange
        String errorMessage = "Ошибка сериализации";
        Throwable cause = new RuntimeException("Причина ошибки");

        // Act
        CurrencyServiceException exception = new CurrencyServiceException(errorMessage, cause);

        // Assert
        assertTrue(java.io.Serializable.class.isAssignableFrom(CurrencyServiceException.class));
    }
}