package com.njdealsandclicks.country;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.country.CountryDTO;


@RestController
@RequestMapping("/api/public/country")
public class CountryPublicController {
    
    private final CountryService countryService;

    public CountryPublicController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/")
    public List<CountryDTO> getAllCountries() {
        return countryService.getAllCountries();
    }
    
}
