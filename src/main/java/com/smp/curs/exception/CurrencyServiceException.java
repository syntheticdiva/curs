package com.smp.curs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CurrencyServiceException extends RuntimeException {
    public CurrencyServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}