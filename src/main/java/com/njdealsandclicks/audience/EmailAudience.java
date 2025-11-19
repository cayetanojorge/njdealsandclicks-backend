package com.njdealsandclicks.audience;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "email_audience",
    indexes = {
        @Index(name = "idx_email_audience_public_id", columnList = "public_id"),
        @Index(name = "idx_email_audience_email", columnList = "email"),
        @Index(name = "idx_email_audience_user_id", columnList = "user_id"),
        @Index(name = "idx_email_audience_is_active", columnList = "is_active")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EmailAudience extends BaseEntity {

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "default_country_code", length = 2)
    private String defaultCountryCode; // es. "IT", "ES"

    @Column(name = "locale", length = 16)
    private String locale; // es. "it_IT"

    @Column(name = "timezone", length = 64)
    private String timezone; // es. "Europe/Rome"

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "consent_source", nullable = false, length = 32)
    private ConsentSource consentSource = ConsentSource.OTHER;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "deactivated_at")
    private ZonedDateTime deactivatedAt;

    /*
    Serve per implementare un link “unsubscribe / disiscrizione**”** senza autenticazione.
    Perché una persona iscritta alla newsletter potrebbe:
        non avere un account
        cliccare il link "Cancellami" direttamente dall’email
        non poter essere autenticata con sessione JWT
    Quando invii un’email includi il link: https://njdealsandclicks.com/unsubscribe?token=ab3498df9234...
    Backend:
        Cerca EmailAudience per token
        Se esiste:
            is_active = false
            deactivated_at = now()
            rimuove ProductPref / MarketPref (se vuoi) 
    È fondamentale per:
        GDPR compliance
        Unsubscribe one-click
        Evitare di esporre UUID o email nel link
        Sicurezza (no brute force: token lungo 64 charset)
    */
    @Column(name="unsubscribe_token", unique=true, length=64)
    private String unsubscribeToken;

    @Column(name="gdpr_consent_version", length=16)
    private String gdprConsentVersion;

    // Collegamento FACOLTATIVO al futuro utente registrato
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
        if (this.email != null) this.email = this.email.trim().toLowerCase();
        if (unsubscribeToken == null) {
            this.unsubscribeToken = UUID.randomUUID().toString().replace("-", "");
        }
    }
}
