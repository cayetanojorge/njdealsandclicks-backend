package com.njdealsandclicks.subscription;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "subscription", 
    indexes = {
        @Index(name = "idx_subscription_public_id", columnList = "publicId"),
        @Index(name = "idx_subscription_plan_name", columnList = "planName")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseEntity{
    
    // @Id
    // @GeneratedValue(strategy =  GenerationType.UUID)
    // private UUID id;

    // @Column(nullable = false, unique = true)
    // @Pattern(regexp = "sub_[a-zA-Z0-9]{10}")
    // private String publicId;

    @Column(nullable = false, unique = true)
    private String planName; // FREE, PREMIUM, PRO

    @Column(nullable = false)
    private String description; // Descrizione del piano

    @Column(nullable = true)
    private String features; // Funzionalità in formato JSON o stringa delimitata

    @Column(nullable = false)
    @Positive // >=0.00
    private Double price;

    @Column(nullable = true)
    @Positive
    private Double promotionalPrice; // Prezzo promozionale (opzionale)

    @Column(nullable = true)
    private ZonedDateTime promotionEndDate; // Fine della promozione

    @Column(nullable = false)
    @Positive
    private Integer durationInDays; // Durata del piano in giorni

    @Column(nullable = false)
    @Positive
    private Integer maxEmailsPerWeek; // Email settimanali massime

    @Column(nullable = false)
    @Positive
    private Integer maxTrackedProducts; // Prodotti monitorati massimi

    @Column(nullable = false)
    @Positive
    private Integer maxTrackedCategories; // Categorie monitorati massimi

    @Column(nullable = false)
    private Boolean isActive = true; // Indica se il piano è attivo

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
