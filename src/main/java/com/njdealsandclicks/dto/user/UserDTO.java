package com.njdealsandclicks.dto.user;

import java.time.ZonedDateTime;

import com.njdealsandclicks.dto.subscription.SubscriptionDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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

    private ZonedDateTime deactivatedAt;

    @NotBlank
    private String preferredLanguage; // browser data
    
    @NotBlank
    private String timezone; // browser data

    @NotNull
    private SubscriptionDTO subscriptionDTO;

    private ZonedDateTime subscriptionExpirationDate;

    @NotNull
    private ZonedDateTime registrationDate;
}
