package com.njdealsandclicks.dto.user;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.dto.subscription.SubscriptionDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDTO {

    @NotBlank
    private String publicId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private Boolean emailVerified;

    @NotNull
    private Boolean isActive;

    @JsonFormat(shape = JsonFormat.Shape.STRING) // per avere ISO invece di timestamp
    private ZonedDateTime deactivatedAt;

    @NotBlank
    private String preferredLanguage; // browser data
    
    @NotBlank
    private String timezone; // browser data

    @NotNull
    private SubscriptionDTO subscriptionDTO;

    @JsonFormat(shape = JsonFormat.Shape.STRING) // per avere ISO invece di timestamp
    private ZonedDateTime subscriptionExpirationDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING) // per avere ISO invece di timestamp
    private ZonedDateTime registrationDate;
}
