package com.njdealsandclicks.dto.currency;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CurrencyCreateUpdateDTO {

    @NotBlank
    private String code; // "EUR", "USD", "GBP", etc.
    
    @NotBlank
    private String name; // "Euro", "US Dollar", etc.
    
    @NotBlank
    private String symbol; // "€", "$", "£"
}
