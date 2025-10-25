package com.njdealsandclicks.dto.product;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDTO {

    @NotBlank
    private String publicId;

    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    private String brand;

    private List<String> tags;

    private List<String> features;

    @NotBlank
    private String categoryName;

    private Map<String, String> productMarketMap; // per fare mappa di publicID e countryCode
    
}
