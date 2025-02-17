package com.njdealsandclicks.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateUpdateDTO {
    
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String preferredLanguage; // browser data
    
    @NotBlank
    private String timezone; // browser data

    @NotBlank
    private String subscriptionPlanName;
}
