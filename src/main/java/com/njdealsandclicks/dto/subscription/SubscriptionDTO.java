package com.njdealsandclicks.dto.subscription;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubscriptionDTO {
    
    @NotBlank
    private String publicId;

    @NotBlank
    private String planName;

    @NotBlank
    private String description;

    private List<String> features;

    @NotNull
    @PositiveOrZero
    private Double price;

    @Positive
    private Double promotionalPrice;

    private ZonedDateTime promotionEndDate;

    @NotNull
    @PositiveOrZero
    private Integer durationInDays;

    @NotNull
    @Positive
    private Integer maxEmailsPerWeek;

    @NotNull
    @Positive
    private Integer maxTrackedProducts;

    @NotNull
    @Positive
    private Integer maxTrackedCategories;

    @NotNull
    private Boolean isActive;
}
