package com.njdealsandclicks.currency;

import com.njdealsandclicks.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "currency")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Currency extends BaseEntity{

    @NotBlank
    @Column(name = "code", nullable = false, unique = true)
    private String code; // "EUR", "USD", "GBP", etc.

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name; // "Euro", "US Dollar", etc.

    @NotBlank
    @Column(name = "symbol", nullable = false)
    private String symbol; // "€", "$", "£"
}
