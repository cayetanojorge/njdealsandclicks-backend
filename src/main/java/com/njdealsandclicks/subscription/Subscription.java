package com.njdealsandclicks.subscription;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.util.StringListToJsonConverterUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "subscription", 
    indexes = {
        @Index(name = "idx_subscription_public_id", columnList = "public_id"),
        @Index(name = "idx_subscription_plan_name", columnList = "plan_name")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Subscription extends BaseEntity {

    @Column(name = "plan_name", nullable = false, unique = true, updatable = false)
    private String planName; // FREE, PREMIUM, PRO

    @Column(name = "description", nullable = false)
    private String description; // Descrizione del piano

    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "features", columnDefinition = "jsonb", nullable = true)
    private List<String> features;

    @PositiveOrZero // >=0.00
    @Column(name = "price", nullable = false)
    private Double price;

    @Positive
    @Column(name = "promotional_price", nullable = true)
    private Double promotionalPrice; // Prezzo promozionale (opzionale)

    @Column(name = "promotion_end_date", nullable = true)
    private ZonedDateTime promotionEndDate; // Fine della promozione

    @Min(0)
    @Column(name = "duration_in_days", nullable = false)
    private Integer durationInDays; // Durata del piano in giorni

    @Positive
    @Column(name = "max_emails_per_week", nullable = false)
    private Integer maxEmailsPerWeek; // Email settimanali massime

    @Positive
    @Column(name = "max_tracked_products", nullable = false)
    private Integer maxTrackedProducts; // Prodotti monitorati massimi

    @Positive
    @Column(name = "max_tracked_categories", nullable = false)
    private Integer maxTrackedCategories; // Categorie monitorati massimi

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Indica se il piano è attivo

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
