package com.smp.curs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    private CurrencyService currencyService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        currencyService = new CurrencyService(restTemplate);
        ReflectionTestUtils.setField(currencyService, "currencyUrl", "http://example.com/currency");
    }

    @Test
    public void testGetYenExchangeRate_Success() throws Exception {
        String xmlResponse = "<ValCurs><Valute><CharCode>JPY</CharCode><Value>75.00</Value></Valute></ValCurs>";
        when(restTemplate.getForObject(anyString(), Mockito.eq(String.class))).thenReturn(xmlResponse);

        String result = currencyService.getYenExchangeRate();

        assertEquals("75.00", result);
    }

    @Test
    public void testGetYenExchangeRate_NoYen() throws Exception {
        String xmlResponse = "<ValCurs><Valute><CharCode>USD</CharCode><Value>100.00</Value></Valute></ValCurs>";
        when(restTemplate.getForObject(anyString(), Mockito.eq(String.class))).thenReturn(xmlResponse);

        String result = currencyService.getYenExchangeRate();

        assertEquals("Курс йены не найден", result);
    }

    @Test
    public void testGetYenExchangeRate_Error() {
        when(restTemplate.getForObject(anyString(), Mockito.eq(String.class))).thenThrow(new RuntimeException("Ошибка"));

        String result = currencyService.getYenExchangeRate();

        assertEquals("Ошибка получения данных о курсе йены", result);
    }
}