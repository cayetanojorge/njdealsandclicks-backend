package com.njdealsandclicks.pricehistory;

import java.util.Date;

import com.njdealsandclicks.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class PriceHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordedAt;

    // lato PROPRIETARIO della relazione
    // colonna di nome 'product_id' di questa tab è fk che collega record di PriceHistory a record di Product
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
}
