package com.njdealsandclicks.user;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.subscription.Subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "app_user",
    indexes = {
        @Index(name = "idx_user_public_id", columnList = "publicId"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_subscription", columnList = "subscription_id")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity{
    
    // @Id
    // @GeneratedValue(strategy =  GenerationType.UUID)
    // private UUID id;

    // @Column(nullable = false, unique = true)
    // @Pattern(regexp = "user_[a-zA-Z0-9]{10}")
    // private String publicId;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    // per 2FA
    // // @Column(unique = true)
    // // private String phoneNumber;

    // per non eliminare i record degli utenti, filtro per escludere gli utenti disattivati
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = true)
    private ZonedDateTime deactivatedAt;

    @Column(nullable = false)
    private String preferredLanguage; // browser data
    
    @Column(nullable = false)
    private String timezone; // browser data

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false)
    private Subscription subscription;

    @Column(nullable = true)
    private ZonedDateTime subscriptionExpirationDate;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private ZonedDateTime registrationDate;

    @Column(nullable = true)
    private ZonedDateTime updatedAt;

    /* annotazione PrePersist metodo chiamato automaticamente prima che nuovo record user salvato in db */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = ZonedDateTime.now(ZoneId.of("UTC"));
    }
    
}
