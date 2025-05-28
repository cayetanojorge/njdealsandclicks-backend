package com.njdealsandclicks.dto.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CountryCreateUpdateDTO {

    @NotBlank
    private String code; // "EUR", "USD", "GBP", etc.

    @NotBlank
    private String name; // "Euro", "US Dollar", etc.

    @NotBlank
    private String language; // es: "it", "en", "es"

    @NotBlank
    private String locale; // es: "it_IT", "en_GB", "es_ES"

    @NotBlank
    private String timezone; // es: "Europe/Rome"

    private String flagEmoji; // es: "🇮🇹", "🇬🇧", "🇪🇸"

    @NotBlank
    private String currencyCode;
}
