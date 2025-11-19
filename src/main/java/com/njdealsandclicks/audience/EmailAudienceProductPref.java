package com.njdealsandclicks.audience;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.productmarket.ProductMarket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "email_audience_product_pref",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_audience_product_market", columnNames = {"email_audience_id", "product_market_id"})
    },
    indexes = {
        @Index(name = "idx_audience_product_audience", columnList = "email_audience_id"),
        @Index(name = "idx_audience_product_market", columnList = "product_market_id")
    }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class EmailAudienceProductPref extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "email_audience_id", referencedColumnName = "id", nullable = false)
    private EmailAudience emailAudience;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_market_id", referencedColumnName = "id", nullable = false)
    private ProductMarket productMarket;

    @NotNull
    @Column(name = "subscribed_at", nullable = false)
    private ZonedDateTime subscribedAt;

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
