package com.smp.curs.controller;

import com.smp.curs.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyControllerTest {

    @Test
    void index() {
        CurrencyService currencyService = Mockito.mock(CurrencyService.class);
        CurrencyController currencyController = new CurrencyController(currencyService);
        Model model = Mockito.mock(Model.class);

        String yenRate = "123.45";
        when(currencyService.getYenExchangeRate()).thenReturn(yenRate);

        String result = currencyController.index(model);

        assertEquals("index", result);
        verify(model).addAttribute("yenRate", yenRate);
    }
}