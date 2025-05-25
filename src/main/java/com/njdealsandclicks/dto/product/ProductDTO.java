package com.njdealsandclicks.dto.product;

import com.njdealsandclicks.dto.country.CountryDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDTO {

    @NotBlank
    private String publicId;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private CountryDTO countryDTO;
    
    @Positive
    private Double currentPrice;

    @NotBlank
    private String affiliateLink;

    private Double rating;

    private Integer reviewCount;

    @NotBlank
    private String categoryName;
}
