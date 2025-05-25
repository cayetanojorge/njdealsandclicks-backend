package com.njdealsandclicks.dto.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CountryDTO {

    @NotBlank
    private String publicId;

    @NotBlank
    private String code; // ISO 3166-1 alpha-2, es: "IT", "UK", "US", "ES"

    @NotBlank
    private String name; // "Italy", "United Kingdom", "Spain"

    private String locale; // es: "it_IT", "en_GB", "es_ES"

    @NotBlank
    private String timezone; // es: "Europe/Rome"

    private String flagEmoji; // es: "🇮🇹", "🇬🇧", "🇪🇸"

    @NotBlank
    private String currencyCode;
}
