package com.njdealsandclicks.pricehistory;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.njdealsandclicks.productmarket.ProductMarket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(
    name = "price_history",
    indexes = {
        @Index(name = "idx_price_history_product", columnList = "product_id")
    }
)
@Data
public class PriceHistory {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Positive
    @Column(name = "price", nullable = false)
    private Double price;
    
    @NotNull
    @Column(name = "recorded_at", nullable = false)
    private ZonedDateTime recordedAt;

    // lato PROPRIETARIO della relazione
    // colonna di nome 'product_id' di questa tab è fk che collega record di PriceHistory a record di Product
    // @JsonIgnore sotto joincolumn così al servizio get non ci sia ricorsione di oggetti ma si ferma qua
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_market_id", referencedColumnName = "id", nullable = false)
    private ProductMarket productMarket;
}
