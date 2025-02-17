package com.njdealsandclicks.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductCreateUpdateDTO {
    @NotBlank
    private String name;

    private String description;

    @Positive
    private Double currentPrice;

    @NotBlank
    private String affiliateLink;

    @NotBlank
    private String categoryName;
}
