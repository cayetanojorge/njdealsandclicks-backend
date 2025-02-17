package com.njdealsandclicks.dto.pricehistory;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PriceHistoryDTO {
    
    @Positive
    private Double price;
    
    @NotNull
    private ZonedDateTime recordedAt;
}
