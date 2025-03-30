package com.njdealsandclicks.currency;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.currency.CurrencyDTO;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }
    
}
