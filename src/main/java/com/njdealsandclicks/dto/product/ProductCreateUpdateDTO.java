package com.njdealsandclicks.dto.product;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductCreateUpdateDTO {
    
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String countryCode;

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
}
