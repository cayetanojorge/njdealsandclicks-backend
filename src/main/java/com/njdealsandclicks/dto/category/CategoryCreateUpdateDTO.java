package com.njdealsandclicks.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateUpdateDTO {
    
    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    @NotBlank
    private String slug;

    @NotBlank
    private Boolean isActive;

    private Integer displayOrder;

    @NotBlank
    private String nameParentCategory;
}
