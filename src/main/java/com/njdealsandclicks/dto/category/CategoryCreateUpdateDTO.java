package com.njdealsandclicks.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateUpdateDTO {
    @NotBlank
    private String name;
}
