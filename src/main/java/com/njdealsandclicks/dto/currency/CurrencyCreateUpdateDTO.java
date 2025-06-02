package com.njdealsandclicks.dto.currency;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CurrencyCreateUpdateDTO {

    @NotBlank
    private String code; // "EUR", "USD", "GBP", etc.
    
    @NotBlank
    private String name; // "Euro", "US Dollar", etc.
    
    @NotBlank
    private String symbol; // "€", "$", "£"
}
