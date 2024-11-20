package com.smp.curs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class CurrencyService {

    @Value("${currency.url}")
    private String currencyUrl;

    public String getYenExchangeRate() {
        try {
            // создание REST-клиента
            RestTemplate restTemplate = new RestTemplate();

            //  получение XML-ответа
            String xmlResponse = restTemplate.getForObject(currencyUrl, String.class);

            // настройка XML-парсера
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            // поиск элементов валют
            NodeList yenNode = doc.getElementsByTagName("Valute");

            // перебор валют для поиска йены
            for (int i = 0; i < yenNode.getLength(); i++) {
                Element element = (Element) yenNode.item(i);

                // проверка кода валюты
                if ("JPY".equals(element.getElementsByTagName("CharCode").item(0).getTextContent())) {
                    // возврат курса йены
                    return element.getElementsByTagName("Value").item(0).getTextContent();
                }
            }
        } catch (Exception e) {
            // обработка ошибок
            return "Ошибка получения данных о курсе йены";
        }
        return "Курс йены не найден";
    }
}