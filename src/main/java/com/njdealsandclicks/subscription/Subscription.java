package com.njdealsandclicks.subscription;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(name = "subscription", 
    indexes = {
        @Index(name = "idx_subscription_public_id", columnList = "publicId"),
        @Index(name = "idx_subscription_plan_name", columnList = "planName")
    }
)
@Data
public class Subscription {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "sub_[a-zA-Z0-9]{10}")
    private String publicId;

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
}
