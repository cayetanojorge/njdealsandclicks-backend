package com.njdealsandclicks.currency;

import com.njdealsandclicks.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Currency extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String code; // "EUR", "USD", "GBP", etc.

    @Column(nullable = false)
    private String name; // "Euro", "US Dollar", etc.

    @Column(nullable = false, unique = true)
    private String symbol; // "€", "$", "£"
}
