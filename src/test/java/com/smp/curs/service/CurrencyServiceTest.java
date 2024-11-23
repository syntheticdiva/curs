package com.smp.curs.service;

import com.smp.curs.exception.CurrencyServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    private static final String MOCK_CURRENCY_URL = "http://test-currency-api.com";
    private static final String VALID_XML_RESPONSE =
            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>" +
                    "<ValCurs Date=\"14.06.2023\" name=\"Foreign Currency Market\">" +
                    "  <Valute ID=\"R01010\">" +
                    "    <NumCode>392</NumCode>" +
                    "    <CharCode>JPY</CharCode>" +
                    "    <Nominal>100</Nominal>" +
                    "    <Name>Японских иен</Name>" +
                    "    <Value>51.4938</Value>" +
                    "  </Valute>" +
                    "</ValCurs>";

    @BeforeEach
    void setUp() {
        // Устанавливаем тестовый URL через ReflectionTestUtils
        ReflectionTestUtils.setField(currencyService, "currencyUrl", MOCK_CURRENCY_URL);
    }

    @Test
    void testGetYenExchangeRate_Success() {
        // Подготовка
        when(restTemplate.getForObject(MOCK_CURRENCY_URL, String.class))
                .thenReturn(VALID_XML_RESPONSE);

        // Выполнение
        String result = currencyService.getYenExchangeRate();

        // Проверка
        assertEquals("51.4938", result);
        verify(restTemplate).getForObject(MOCK_CURRENCY_URL, String.class);
    }

    @Test
    void testGetYenExchangeRate_NullResponse() {
        // Подготовка
        when(restTemplate.getForObject(MOCK_CURRENCY_URL, String.class))
                .thenReturn(null);

        // Выполнение и проверка
        CurrencyServiceException exception = assertThrows(CurrencyServiceException.class,
                () -> currencyService.getYenExchangeRate());

        // Проверяем сообщение об ошибке
        assertEquals("Ошибка при парсинге XML ответа", exception.getMessage());
    }
    @Test
    void testGetYenExchangeRate_HttpClientError() {
        // Подготовка
        when(restTemplate.getForObject(MOCK_CURRENCY_URL, String.class))
                .thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND));

        // Выполнение и проверка
        CurrencyServiceException exception = assertThrows(CurrencyServiceException.class,
                () -> currencyService.getYenExchangeRate());

        assertTrue(exception.getMessage().contains("Ошибка при обращении к внешнему API"));
    }

    @Test
    void testGetYenExchangeRate_RestClientError() {
        // Подготовка
        when(restTemplate.getForObject(MOCK_CURRENCY_URL, String.class))
                .thenThrow(new RestClientException("Connection error"));

        // Выполнение и проверка
        assertThrows(CurrencyServiceException.class,
                () -> currencyService.getYenExchangeRate());
    }

    @Test
    void testGetYenExchangeRate_XmlParseError() {
        // Подготовка
        String invalidXml = "Invalid XML";
        when(restTemplate.getForObject(MOCK_CURRENCY_URL, String.class))
                .thenReturn(invalidXml);

        // Выполнение и проверка
        assertThrows(CurrencyServiceException.class,
                () -> currencyService.getYenExchangeRate());
    }
}