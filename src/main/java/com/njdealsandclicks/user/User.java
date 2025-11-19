package com.njdealsandclicks.user;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.newsletter.Newsletter;
import com.njdealsandclicks.subscription.Subscription;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/*
index per:
public_id - Cerchi utenti tramite publicId (es. API REST pubbliche)
email - Cerchi utenti per login, autenticazione o admin panel
subscription_id - Vuoi sapere quali utenti hanno un certo piano
subscription_expiration_date - Vuoi fare query tipo "quali utenti scadono oggi?" o cron job
 */
@Entity
@Table(
    name = "app_user",
    indexes = {
        @Index(name = "idx_user_public_id", columnList = "public_id"),
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_subscription", columnList = "subscription_id"),
        @Index(name = "idx_user_subscription_expiration_date", columnList = "subscription_expiration_date")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class User extends BaseEntity{

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    // per 2FA
    // // @Column(unique = true)
    // // private String phoneNumber;

    // per non eliminare i record degli utenti, filtro per escludere gli utenti disattivati
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "deactivated_at", nullable = true)
    private ZonedDateTime deactivatedAt;

    @NotBlank
    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage; // browser data
    
    @NotBlank
    @Column(name = "timezone", nullable = false)
    private String timezone; // browser data

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", referencedColumnName = "id", nullable = false)
    private Subscription subscription;

    @Column(name = "subscription_expiration_date", nullable = true)
    private ZonedDateTime subscriptionExpirationDate;

    @NotNull
    @Column(name = "registration_date", nullable = false, updatable = false)
    private ZonedDateTime registrationDate;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // se utente eliminato elimino newsletter
    private Newsletter newsletter;

    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;

    /* annotazione PrePersist metodo chiamato automaticamente prima che nuovo record user salvato in db */
    @PrePersist
    protected void onCreate() {
        this.registrationDate = ZonedDateTime.now(ZoneId.of("UTC"));
    }
    
}
