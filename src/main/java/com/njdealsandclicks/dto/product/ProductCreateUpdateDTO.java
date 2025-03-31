package com.njdealsandclicks.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductCreateUpdateDTO {
    
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String currencyCode;

    @Positive
    private Double currentPrice;

    @NotBlank
    private String affiliateLink;

    private Double rating;

    private Integer reviewCount;

    @NotBlank
    private String categoryName;
}
