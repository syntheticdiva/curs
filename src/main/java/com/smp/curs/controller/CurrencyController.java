package com.smp.curs.controller;

import com.smp.curs.service.CurrencyService;
import com.smp.curs.exception.CurrencyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;

@Slf4j
@Controller
public class CurrencyController {

    private final CurrencyService currencyService;

    private static final String ERROR_VIEW = "error";

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        try {
            String yenRate = currencyService.getYenExchangeRate();
            model.addAttribute("yenRate", yenRate);
            return "index";
        } catch (CurrencyServiceException e) {
            log.error("Ошибка при получении курса йены", e);
            model.addAttribute("errorMessage", "Не удалось получить курс йены");
            model.addAttribute("technicalDetails", e.getMessage());
            return ERROR_VIEW;
        } catch (Exception e) {
            log.error("Непредвиденная ошибка", e);
            model.addAttribute("errorMessage", "Произошла системная ошибка");
            model.addAttribute("technicalDetails", "Пожалуйста, попробуйте позже");
            return ERROR_VIEW;
        }
    }

    @GetMapping("/**")
    public String handleUnknownPaths(Model model) {
        log.error("Запрашиваемая страница не найдена");
        model.addAttribute("errorMessage", "Запрашиваемая страница не найдена. Проверьте правильность URL.");
        model.addAttribute("technicalDetails", "");
        model.addAttribute("timestamp", new Date());
        return ERROR_VIEW;
    }
}