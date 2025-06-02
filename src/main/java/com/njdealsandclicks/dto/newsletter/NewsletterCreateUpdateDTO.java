package com.njdealsandclicks.dto.newsletter;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.dto.product.ProductDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewsletterCreateUpdateDTO {
    
    @Email
    @NotBlank
    private String userEmail;

    @NotNull
    private Boolean generalNewsletter;

    private List<ProductDTO> productDTOs;

    private List<CategoryDTO> categoryDTOs;
}
