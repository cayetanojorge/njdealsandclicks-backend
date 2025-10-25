package com.njdealsandclicks.dto.productmarket;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.njdealsandclicks.config.output.TwoDecimalDoubleSerializer;
import com.njdealsandclicks.country.Country;
import com.njdealsandclicks.dto.pricehistory.PriceHistoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductMarketDetailsDTO {
  
    @NotNull
    private Country country;

    private String externalSku; // es: ASIN nel mercato

    @NotBlank
    private String affiliateLink;

    @NotNull
    @Positive
    @JsonSerialize(using = TwoDecimalDoubleSerializer.class)
    private Double currentPrice;

    @NotNull
    private Double rating;

    @NotNull
    private Integer reviewCount;

    @NotNull
    private Boolean isAvailable;

    private String imageUrl; // override locale (se presente, preferisci questo)

    private ZonedDateTime lastCheckedAt; // utile per job check ogni 3 gg

    private List<PriceHistoryDTO> priceHistoryDTOs;

    // utile per lo switcher in UI
    private Map<String, Boolean> availabilityByCountry;  // es: {"IT":true,"ES":false,...}
}
