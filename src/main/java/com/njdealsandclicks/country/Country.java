package com.njdealsandclicks.country;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.currency.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "country")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Country extends BaseEntity{

    @NotNull
    @NotBlank
    @Column(name = "code", unique = true, nullable = false, length = 2)
    private String code; // ISO 3166-1 alpha-2, es: "IT", "UK", "US", "ES"

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name; // "Italy", "United Kingdom", "Spain"

    @Column(name = "language", nullable = false, length = 2)
    private String language; // es: "it", "en", "es"

    @NotBlank
    @Column(name = "locale", nullable = true)
    private String locale; // es: "it_IT", "en_GB", "es_ES"

    @NotBlank
    @Column(name = "timezone", nullable = true)
    private String timezone; // es: "Europe/Rome"

    @Column(name = "flag_emoji", nullable = true)
    private String flagEmoji; // es: "🇮🇹", "🇬🇧", "🇪🇸"

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id", referencedColumnName = "id", nullable = false)
    private Currency currency;
}
