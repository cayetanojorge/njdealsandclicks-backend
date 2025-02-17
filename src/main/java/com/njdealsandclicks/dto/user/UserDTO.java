package com.njdealsandclicks.dto.user;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank
    private String publicId;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private Boolean emailVerified;

    @NotBlank
    private String preferredLanguage; // browser data
    
    @NotBlank
    private String timezone; // browser data

    @NotBlank
    private String subscriptionPlanName;

    @NotNull
    private ZonedDateTime subscriptionExpirationDate;

    @NotBlank
    private String emailFrequency; // LOW, MEDIUM, HIGH

    @NotNull
    private ZonedDateTime registrationDate;
}
