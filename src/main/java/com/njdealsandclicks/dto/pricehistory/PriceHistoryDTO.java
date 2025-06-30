package com.njdealsandclicks.dto.pricehistory;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.njdealsandclicks.config.output.TwoDecimalDoubleSerializer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PriceHistoryDTO {
    
    @NotNull
    @Positive
    @JsonSerialize(using = TwoDecimalDoubleSerializer.class)
    private Double price;
    
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING) // per avere ISO invece di timestamp
    private ZonedDateTime recordedAt;
}
