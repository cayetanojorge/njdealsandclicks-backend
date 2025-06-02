package com.njdealsandclicks.dto.category;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CategoryDTO {
    
    @NotBlank
    private String publicId;

    @NotBlank
    private String name;

    private String description;

    private String imageUrl;

    @NotBlank
    private String slug;

    @NotNull
    private Boolean isActive;

    private Integer displayOrder;

    @NotBlank
    private String nameParentCategory;
}
