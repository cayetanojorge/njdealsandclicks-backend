package com.njdealsandclicks.product;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.productmarket.ProductMarket;
import com.njdealsandclicks.util.StringListToJsonConverterUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Definisci l'entità Product che rappresenta una tabella nel database.
 */


// Se il prezzo corrente viene utilizzato spesso per ordinare i prodotti (es., "ordina dal più economico al più costoso"):
@Entity /* per indicate che sia tabella in db */
@Table(
    name = "product",
    // uniqueConstraints = { @UniqueConstraint(columnNames = {"affiliate_link", "country_id"}) },
    indexes = {
        @Index(name = "idx_product_public_id", columnList = "public_id"),
        // @Index(name = "idx_product_current_price", columnList = "current_price"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        // @Index(name = "idx_product_category_price", columnList = "category_id, current_price"), /* index composto: ordinare frequentemente risultati per prezzo all'interno di una categoria */
        @Index(name = "idx_product_tags_gin", columnList = "tags"), // (solo descrittivo, fare con query in db)
        @Index(name = "idx_product_features_gin", columnList = "features"), // (solo descrittivo, fare con query in db)
        // @Index(name = "idx_product_country", columnList = "country_id")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(name = "brand", nullable = true)
    private String brand;

    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "tags", columnDefinition = "jsonb", nullable = true)
    private List<String> tags; // migliorare la ricerca e il SEO del sito
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "features", columnDefinition = "jsonb", nullable = true)
    private List<String> features; // caratteristiche specifiche, ie: "Schermo OLED", "Batteria da 5000mAh"
    
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false) // name e' nome della colonna, refe...Name e' nome della colonna di tabella Category a cui si fa riferimento
    private Category category;

    // opzionale, utile per UI (pallini di disponibilità per mercato)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductMarket> productMarkets = new ArrayList<>();

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
    
    /* 
     * GIN sta per Generalized Inverted Index. È un tipo di indice PostgreSQL specifico per colonne JSONB, array, full-text search. 
     * Ti serve se vorrai eseguire query come:
     *  SELECT * FROM product WHERE tags @> '["offerta", "gaming"]';
     *  SELECT * FROM product WHERE features @> '["oled"]';
     * abilitarlo:
     * Puoi farlo direttamente via migration Flyway o liquibase o uno script SQL manuale:
     *  CREATE INDEX idx_product_tags_gin ON product USING GIN (tags); 
     *  CREATE INDEX idx_product_features_gin ON product USING GIN (features);
    */
}
