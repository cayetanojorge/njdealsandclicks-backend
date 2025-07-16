package com.njdealsandclicks.dto.article;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ArticleCreateUpdateDTO {

    @NotBlank
    private String title;

    private String excerpt;

    @NotBlank
    private String content; // in Markdown o HTML

    private String imageUrl;

    private List<String> tags;

    @NotNull
    private Boolean isPublished; // per gestire articoli in bozza, mostrare nel frontend solo quando is_published = true

    private List<String> productPublicIds;

}
