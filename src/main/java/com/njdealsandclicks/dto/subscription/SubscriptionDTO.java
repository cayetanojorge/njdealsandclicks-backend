package com.njdealsandclicks.dto.subscription;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscriptionDTO {
    
    @NotBlank
    private String publicId;

    @NotBlank
    private String planName;

    @NotBlank
    private String description;

    @NotBlank
    private String features;

    @Positive
    private Double price;

    private Double promotionalPrice;

    private ZonedDateTime promotionEndDate;

    @Positive
    private Integer durationInDays;

    @Positive
    private Integer maxEmailsPerWeek;

    @Positive
    private Integer maxTrackedProducts;

    @Positive
    private Integer maxTrackedCategories;

    private Boolean isActive;
}
