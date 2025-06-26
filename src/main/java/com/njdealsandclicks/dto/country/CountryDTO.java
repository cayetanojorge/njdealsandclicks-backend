package com.njdealsandclicks.dto.country;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.dto.currency.CurrencyDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CountryDTO {

    @NotBlank
    private String publicId;

    @NotBlank
    private String code; // ISO 3166-1 alpha-2, es: "IT", "UK", "US", "ES"

    @NotBlank
    private String name; // "Italy", "United Kingdom", "Spain"

    @NotBlank
    private String language; // es: "it", "en", "es"

    @NotBlank
    private String locale; // es: "it_IT", "en_GB", "es_ES"

    @NotBlank
    private String timezone; // es: "Europe/Rome"

    private String flagEmoji; // es: "🇮🇹", "🇬🇧", "🇪🇸"

    @NotBlank
    private CurrencyDTO currencyDTO;
}
