package com.njdealsandclicks.audience;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.country.Country;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "email_audience_market_pref",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_audience_market", columnNames = {"email_audience_id", "country_id"})
    },
    indexes = {
        @Index(name = "idx_audience_market_audience", columnList = "email_audience_id"),
        @Index(name = "idx_audience_market_country", columnList = "country_id")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class EmailAudienceMarketPref extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // default fetch EAGER
    @JoinColumn(name = "email_audience_id", referencedColumnName = "id", nullable = false)
    private EmailAudience emailAudience;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    private Country country;

    // opzionale, utile per audit/ordinamento in futuro
    @Column(name = "note", length = 120)
    private String note;
}
