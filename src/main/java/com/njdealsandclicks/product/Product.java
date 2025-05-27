package com.njdealsandclicks.product;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.country.Country;
import com.njdealsandclicks.pricehistory.PriceHistory;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Definisci l'entità Product che rappresenta una tabella nel database.
 */


// Se il prezzo corrente viene utilizzato spesso per ordinare i prodotti (es., "ordina dal più economico al più costoso"):
@Entity /* per indicate che sia tabella in db */
@Table(
    name = "product",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"affiliate_link", "country_id"}) },
    indexes = {
        @Index(name = "idx_product_public_id", columnList = "public_id"),
        @Index(name = "idx_product_current_price", columnList = "current_price"),
        @Index(name = "idx_product_category", columnList = "category_id"),
        @Index(name = "idx_product_category_price", columnList = "category_id, current_price"), /* index composto: ordinare frequentemente risultati per prezzo all'interno di una categoria */
        @Index(name = "idx_product_tags_gin", columnList = "tags"), // (solo descrittivo, fare con query in db)
        @Index(name = "idx_product_features_gin", columnList = "features") // (solo descrittivo, fare con query in db)
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    private Country country;

    @Positive
    @Column(name = "current_price", nullable = true)
    private Double currentPrice;
    
    // todo-future creare entita' ProductMarket con alcune caratteristiche di Product, futuro ampliamento in altri mercati: UK, USA, ecc.

    @NotBlank
    @Column(name = "affiliate_link", nullable = false)
    private String affiliateLink;

    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "brand", nullable = true)
    private String brand;

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

    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "tags", columnDefinition = "jsonb", nullable = true)
    private List<String> tags; // migliorare la ricerca e il SEO del sito
    
    @Convert(converter = StringListToJsonConverterUtil.class)
    @Column(name = "features", columnDefinition = "jsonb", nullable = true)
    private List<String> features; // caratteristiche specifiche, ie: "Schermo OLED", "Batteria da 5000mAh"

    @Column(name = "image_url", nullable = true)
    private String imageUrl;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false) // name e' nome della colonna, refe...Name e' nome della colonna di tabella Category a cui si fa riferimento
    private Category category;
    
    // lato NON proprietario
    /* one-to-many & mappedBy presenti qua = indico questo lato come non proprietario della relazione tra due entita'.
        Evitiamo in questa tabella la creazione di colonna aggiuntiva.
        mappedBy indica quale proprieta' nell'altra entita' (in questo caso Product in PriceHistory) mappa questa relazione.
        Quindi la tab price_history avra' colonna foreign key che fa riferimento a tabella product.
      
        Nota: è relazione oneToMany - lato many PriceHistory detiene fk a product, lato one Product accede ai dati tramite join
        non c'e' colonna che fa riferimento a price_history; la relazione e' gestita in memoria dell'applicazione da Hibernate tramite lista priceHistories.
        (se ci fosse colonna riferimento a PriceHistory per ogni relazione, allora impossibile rappresentare relazione OneToMany, prodotto con più prezzi storici,
        questo perche' una colonna non puo' contenere molteplici riferimenti in una tabella relazionale) */
    // cascade - Qualsiasi operazione eseguita su un prodotto (ad esempio, salvataggio, aggiornamento o eliminazione) viene propagata a tutti i record di PriceHistory associati.
    //      Esempio: Se elimini un prodotto, tutti i suoi record di storico prezzi verranno eliminati automaticamente.
    // orphanRemoval - Se un record di PriceHistory viene scollegato dal prodotto (rimosso dalla lista priceHistories), verrà eliminato automaticamente dal database.
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
