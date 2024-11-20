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

    private final RestTemplate restTemplate;

    @Value("${currency.url}")
    private String currencyUrl;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getYenExchangeRate() {
        try {
            String xmlResponse = restTemplate.getForObject(currencyUrl, String.class);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

            NodeList yenNode = doc.getElementsByTagName("Valute");
            for (int i = 0; i < yenNode.getLength(); i++) {
                Element element = (Element) yenNode.item(i);
                if ("JPY".equals(element.getElementsByTagName("CharCode").item(0).getTextContent())) {
                    return element.getElementsByTagName("Value").item(0).getTextContent();
                }
            }
        } catch (Exception e) {
            return "Ошибка получения данных о курсе йены";
        }
        return "Курс йены не найден";
    }
}