package com.njdealsandclicks.dto.subscription;

import java.time.ZonedDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class SubscriptionCreateUpdateDTO {
    
    @NotBlank
    private String planName;

    @NotBlank
    private String description;

    @NotBlank
    private List<String> features;

    @PositiveOrZero
    private Double price;

    private Double promotionalPrice;

    private ZonedDateTime promotionEndDate;

    @PositiveOrZero
    private Integer durationInDays;

    @Positive
    private Integer maxEmailsPerWeek;

    @Positive
    private Integer maxTrackedProducts;

    @Positive
    private Integer maxTrackedCategories;

    private Boolean isActive;
}
