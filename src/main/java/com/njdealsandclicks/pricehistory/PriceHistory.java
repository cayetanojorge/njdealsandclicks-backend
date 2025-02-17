package com.njdealsandclicks.pricehistory;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.njdealsandclicks.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity
@Table(indexes = {
    @Index(name = "idx_price_history_product", columnList = "product_id")
})
@Data
public class PriceHistory {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    // @Column(nullable = false, unique = true)
    // @Pattern(regexp = "prod_[a-zA-Z0-9]{10}")
    // private String publicId;

    @Positive
    private Double price;
    
    // String currency 

    @Column(name = "recorded_at", nullable = false)
    private ZonedDateTime recordedAt;

    // lato PROPRIETARIO della relazione
    // colonna di nome 'product_id' di questa tab è fk che collega record di PriceHistory a record di Product
    // @JsonIgnore sotto joincolumn così al servizio get non ci sia ricorsione di oggetti ma si ferma qua 
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
}
