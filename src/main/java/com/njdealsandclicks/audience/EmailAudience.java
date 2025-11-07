package com.njdealsandclicks.audience;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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
@EqualsAndHashCode(callSuper = true)
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
    }
}
