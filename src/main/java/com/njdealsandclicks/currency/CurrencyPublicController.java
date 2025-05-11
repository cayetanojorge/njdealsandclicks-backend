package com.njdealsandclicks.currency;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.currency.CurrencyDTO;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/public/currency")
public class CurrencyPublicController {
    
    private final CurrencyService currencyService;

    public CurrencyPublicController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }
    
}
