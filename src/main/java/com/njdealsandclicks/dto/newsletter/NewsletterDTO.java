package com.njdealsandclicks.dto.newsletter;

import java.util.List;

import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.user.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewsletterDTO {
 
    @NotBlank
    private String publicId;

    @NotNull
    private UserDTO userDTO;

    @NotNull
    private Boolean generalNewsletter;

    private List<ProductDTO> productDTOs;

    private List<CategoryDTO> categoryDTOs;
}
