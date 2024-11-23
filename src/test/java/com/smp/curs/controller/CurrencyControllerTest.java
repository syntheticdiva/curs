package com.smp.curs.controller;

import com.smp.curs.service.CurrencyService;
import com.smp.curs.exception.CurrencyServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @Mock
    private Model model;

    private CurrencyController currencyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyController = new CurrencyController(currencyService);
    }

    @Test
    void testIndex_SuccessfulCurrencyRetrieval() throws Exception {
        // Arrange
        String expectedYenRate = "130.50";
        when(currencyService.getYenExchangeRate()).thenReturn(expectedYenRate);

        // Act
        String viewName = currencyController.index(model);

        // Assert
        assertEquals("index", viewName);
        verify(model).addAttribute("yenRate", expectedYenRate);
    }

    @Test
    void testIndex_CurrencyServiceException() throws Exception {
        // Arrange
        String errorMessage = "Ошибка сервиса";
        CurrencyServiceException exception = new CurrencyServiceException(errorMessage, null);
        when(currencyService.getYenExchangeRate()).thenThrow(exception);

        // Act
        String viewName = currencyController.index(model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Не удалось получить курс йены");
        verify(model).addAttribute("technicalDetails", errorMessage);
    }

    @Test
    void testIndex_UnexpectedException() throws Exception {
        // Arrange
        RuntimeException exception = new RuntimeException("Системная ошибка");
        when(currencyService.getYenExchangeRate()).thenThrow(exception);

        // Act
        String viewName = currencyController.index(model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Произошла системная ошибка");
        verify(model).addAttribute("technicalDetails", "Пожалуйста, попробуйте позже");
    }

    @Test
    void testHandleUnknownPaths() {
        // Act
        String viewName = currencyController.handleUnknownPaths(model);

        // Assert
        assertEquals("error", viewName);
        verify(model).addAttribute("errorMessage", "Запрашиваемая страница не найдена. Проверьте правильность URL.");
        verify(model).addAttribute("technicalDetails", "");
        verify(model).addAttribute(eq("timestamp"), any());
    }
}