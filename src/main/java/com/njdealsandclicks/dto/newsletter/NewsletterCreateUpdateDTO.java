package com.njdealsandclicks.dto.newsletter;

import java.util.List;

import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.user.UserCreateUpdateDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewsletterCreateUpdateDTO {
    
    @NotNull
    private UserCreateUpdateDTO userCreateUpdateDTO;

    @NotNull
    private Boolean generalNewsletter;

    private List<ProductDTO> productDTOs;

    private List<CategoryDTO> categoryDTOs;
}
