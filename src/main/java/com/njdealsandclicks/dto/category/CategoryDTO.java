package com.njdealsandclicks.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    
    @NotBlank
    private String publicId;

    @NotBlank
    private String name;
}
