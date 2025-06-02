package com.njdealsandclicks.dto.product;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.dto.country.CountryDTO;
import com.njdealsandclicks.dto.pricehistory.PriceHistoryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDetailsDTO {

    @NotBlank
    private String publicId;
    
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private CountryDTO countryDTO;
    
    @NotNull
    @Positive
    private Double currentPrice;

    @NotBlank
    private String affiliateLink;

    @NotNull
    private Double rating;

    @NotNull
    private Integer reviewCount;

    private String imageUrl;
    
    private String brand;

    private List<String> tags;

    private List<String> features;

    @NotBlank
    private String categoryName;

    private List<PriceHistoryDTO> priceHistoryDTOs;
}
