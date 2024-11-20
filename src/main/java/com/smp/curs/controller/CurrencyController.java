package com.smp.curs.controller;


import com.smp.curs.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        String yenRate = currencyService.getYenExchangeRate();
        model.addAttribute("yenRate", yenRate);
        return "index";
    }
}