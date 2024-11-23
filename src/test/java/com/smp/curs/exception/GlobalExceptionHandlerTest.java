package com.smp.curs.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private Model model;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        model = mock(Model.class);
    }

    @Test
    void testHandleCurrencyNotFoundException() {
        // Arrange
        CurrencyNotFoundException exception = new CurrencyNotFoundException("Валюта не найдена");

        // Act
        String viewName = exceptionHandler.handleCurrencyNotFound(exception, model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Курс не найден");
        verify(model).addAttribute("technicalDetails", "Валюта не найдена");
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void testHandleCurrencyServiceException() {
        // Arrange
        CurrencyServiceException exception = new CurrencyServiceException("Ошибка при запросе", new RuntimeException());

        // Act
        String viewName = exceptionHandler.handleCurrencyServiceException(exception, model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Ошибка получения данных о курсе валют");
        verify(model).addAttribute("technicalDetails", "Пожалуйста, попробуйте позже");
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void testHandleNoHandlerFoundException() {
        // Arrange
        NoHandlerFoundException exception = mock(NoHandlerFoundException.class);
        when(exception.getMessage()).thenReturn("Страница не существует");

        // Act
        String viewName = exceptionHandler.handleNoHandlerFound(exception, model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Запрашиваемая страница не найдена. Проверьте правильность URL.");
        verify(model).addAttribute("technicalDetails", "Страница не существует");
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void testHandleAllUncaughtExceptions() {
        // Arrange
        Exception exception = new RuntimeException("Неизвестная ошибка");

        // Act
        String viewName = exceptionHandler.handleAllUncaughtExceptions(exception, model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Произошла системная ошибка");
        verify(model).addAttribute("technicalDetails", "Пожалуйста, попробуйте позже или обратитесь к администратору");
        verify(model).addAttribute(eq("timestamp"), any());
    }

    @Test
    void testCreateErrorResponse() {
        // Arrange
        String userMessage = "Тестовое сообщение";
        String technicalMessage = "Техническая информация";

        // Act
        String viewName = exceptionHandler.createErrorResponse(userMessage, technicalMessage, model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", userMessage);
        verify(model).addAttribute("technicalDetails", technicalMessage);
        verify(model).addAttribute(eq("timestamp"), any());
    }
}