package com.njdealsandclicks.currency;

import com.njdealsandclicks.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "currency")
@Data
@EqualsAndHashCode(callSuper = true)
public class Currency extends BaseEntity{

    @Column(name = "code", nullable = false, unique = true)
    private String code; // "EUR", "USD", "GBP", etc.

    @Column(name = "name", nullable = false)
    private String name; // "Euro", "US Dollar", etc.

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol; // "€", "$", "£"
}
