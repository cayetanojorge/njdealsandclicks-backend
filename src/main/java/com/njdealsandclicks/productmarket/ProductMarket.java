package com.njdealsandclicks.productmarket;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.country.Country;
import com.njdealsandclicks.pricehistory.PriceHistory;
import com.njdealsandclicks.product.Product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
  name = "product_market",
  uniqueConstraints = { @UniqueConstraint(columnNames = {"product_id","country_id"}) },
  indexes = {
    @Index(name = "idx_product_market_product", columnList = "product_id"),
    @Index(name = "idx_product_market_country", columnList = "country_id"),
    @Index(name = "idx_product_market_current_price", columnList = "current_price"),
    @Index(name = "idx_product_market_country_price", columnList = "country_id, current_price")
  }
)
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ProductMarket extends BaseEntity{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "id", nullable = false)
    private Country country;

    // opzionali ma utili per crawling/PAAPI
    @Column(name = "external_sku")
    private String externalSku; // es: ASIN nel mercato

    @NotBlank
    @Column(name = "affiliate_link", nullable = false)
    private String affiliateLink;

    @NotNull
    @PositiveOrZero
    @Column(name = "current_price", nullable = false)
    private Double currentPrice;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    @NotNull
    @PositiveOrZero
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;

    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "image_url")
    private String imageUrl; // override locale (se presente, preferisci questo)

    @Column(name = "last_checked_at")
    private ZonedDateTime lastCheckedAt; // utile per job check ogni 3 gg

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

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
    @OneToMany(mappedBy = "productMarket", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistories = new ArrayList<>();

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_at", nullable = true)
    private ZonedDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
    
}
