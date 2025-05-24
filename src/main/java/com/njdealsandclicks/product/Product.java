package com.njdealsandclicks.product;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.currency.Currency;
import com.njdealsandclicks.pricehistory.PriceHistory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Definisci l'entità Product che rappresenta una tabella nel database.
 */


// Se il prezzo corrente viene utilizzato spesso per ordinare i prodotti (es., "ordina dal più economico al più costoso"):
@Entity /* per indicate che sia tabella in db */
@Table(indexes = {
    @Index(name = "idx_product_public_id", columnList = "publicId"),
    @Index(name = "idx_product_current_price", columnList = "currentPrice"),
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_category_price", columnList = "category_id, currentPrice") /* index composto: ordinare frequentemente risultati per prezzo all'interno di una categoria */
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity{

    @Column(nullable = false)
    @NotBlank
    private String name;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id", referencedColumnName = "id", nullable = false)
    private Currency currency;

    @Column(nullable = true)
    @Positive
    private Double currentPrice;
    
    // TODO creare entita' ProductMarket con alcune caratteristiche di Product, futuro ampliamento in altri mercati: UK, USA, ecc.

    @Column(nullable = false, unique = true)
    @NotBlank
    private String affiliateLink;

    @Column(nullable = true)
    private Double rating;

    @Column(nullable = true)
    private Integer reviewCount;

    @Column(nullable = false)
    private Boolean availability = true;

    @Column(nullable = true)
    private String brand;

    @Column(nullable = true)
    private List<String> tags; // migliorare la ricerca e il SEO del sito
    
    @Column(nullable = true)
    private List<String> features; // caratteristiche specifiche, ie: "Schermo OLED", "Batteria da 5000mAh"

    @Column(nullable = true)
    private String imageUrl;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id") // name e' nome della colonna, refe...Name e' nome della colonna di tabella Category a cui si fa riferimento
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

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
