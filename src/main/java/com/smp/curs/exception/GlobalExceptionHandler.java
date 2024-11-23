package com.smp.curs.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private String errorResponse;

    @ExceptionHandler(CurrencyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCurrencyNotFound(CurrencyNotFoundException e, Model model) {
        return createErrorResponse("Курс не найден", e.getMessage(), model);
    }

    @ExceptionHandler(CurrencyServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleCurrencyServiceException(CurrencyServiceException e, Model model) {
        logError("Ошибка сервиса валют", e);
        return createErrorResponse("Ошибка получения данных о курсе валют", "Пожалуйста, попробуйте позже", model);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException e, Model model) {
        logError("Запрашиваемая страница не найдена", e);
        return createErrorResponse("Запрашиваемая страница не найдена. Проверьте правильность URL.", e.getMessage(), model);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleHttpClientError(HttpClientErrorException e, Model model) {
        logError("Ошибка клиента HTTP", e);
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllUncaughtExceptions(Exception e, Model model) {
        logError("Произошла системная ошибка", e);
        return createErrorResponse("Произошла системная ошибка", "Пожалуйста, попробуйте позже или обратитесь к администратору", model);
    }

    private String createErrorResponse(String userMessage, String technicalMessage, Model model) {
        model.addAttribute("errorMessage", userMessage);
        model.addAttribute("technicalDetails", technicalMessage);
        model.addAttribute("timestamp", new Date());
        return "error"; // возвращаем страницу с ошибкой
    }
    private void logError(String message, Exception e) {
        MDC.put("exceptionClass", e.getClass().getName());
        MDC.put("exceptionMessage", e.getMessage());
        log.error(message, e);
        MDC.clear();
    }
}