package com.smp.curs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.smp.curs.exception.CurrencyNotFoundException;
import com.smp.curs.exception.CurrencyServiceException;

import java.io.StringReader;
import java.util.Optional;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;

    @Value("${currency.url}")
    private String currencyUrl;

    private static final String CURRENCY_CODE_JPY = "JPY";
    private static final String EXTERNAL_API_ERROR_MESSAGE = "Ошибка при обращении к внешнему API";
    private static final String XML_PARSE_ERROR_MESSAGE = "Ошибка при парсинге XML ответа";

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getYenExchangeRate() {
        try {
            String xmlResponse = restTemplate.getForObject(currencyUrl, String.class);
            return Optional.ofNullable(xmlResponse)
                    .map(this::parseYenRate)
                    .orElseThrow(() -> new CurrencyNotFoundException("Ответ от API пустой или недоступен"));

        } catch (HttpClientErrorException e) {
            // Логируем ошибку клиента
            System.out.println("HTTP Client Error: " + e.getStatusCode() + " - " + e.getMessage());
            throw new CurrencyServiceException(EXTERNAL_API_ERROR_MESSAGE + ": " + e.getStatusCode() + " - " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new CurrencyServiceException(EXTERNAL_API_ERROR_MESSAGE, e);
        } catch (Exception e) {
            throw new CurrencyServiceException(XML_PARSE_ERROR_MESSAGE, e);
        }
    }

    private String parseYenRate(String xmlResponse) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList yenNode = doc.getElementsByTagName("Valute");
            for (int i = 0; i < yenNode.getLength(); i++) {
                Element element = (Element) yenNode.item(i);
                if (CURRENCY_CODE_JPY.equals(element.getElementsByTagName("CharCode").item(0).getTextContent())) {
                    return element.getElementsByTagName("Value").item(0).getTextContent();
                }
            }

            throw new CurrencyNotFoundException("Курс йены не найден в ответе API");
        } catch (Exception e) {
            throw new CurrencyServiceException(XML_PARSE_ERROR_MESSAGE, e);
        }
    }
}