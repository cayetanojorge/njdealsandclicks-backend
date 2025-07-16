package com.njdealsandclicks.dto.article;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.dto.product.ProductDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ArticleDTO {
    
    @NotBlank
    private String publicId;

    @NotBlank
    private String title;
    
    @NotBlank
    private String slug;

    private String excerpt;

    @NotBlank
    private String content; // in Markdown o HTML

    private String imageUrl;

    private List<String> tags;

    private ZonedDateTime updatedAt;

    private ZonedDateTime publishedAt;

    private Integer readingTimeMinutes; // Mapper con una stima tipo: content.split(" ").length / 200

    private List<ProductDTO> productDTOs;
}
