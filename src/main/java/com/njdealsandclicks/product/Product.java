package com.njdealsandclicks.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;

import com.njdealsandclicks.pricehistory.PriceHistory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
// import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;
import lombok.Data;


/**
 * Definisci l'entità Product che rappresenta una tabella nel database.
 */


// Se il prezzo corrente viene utilizzato spesso per ordinare i prodotti (es., "ordina dal più economico al più costoso"):
// @Table(indexes = {
//     @Index(name = "idx_product_current_price", columnList = "currentPrice")
// })
@Entity /* per indicate che sia tabella in db */
@Data /* @ToString, @EqualsAndHashCode, @Getter / @Setter and @RequiredArgsConstructor */
public class Product {

    @Id /* indica chiave primaria */
    @GeneratedValue(strategy =  GenerationType.IDENTITY) /* indica generazione automatica */
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    // @Column(nullable = true)
    private Double currentPrice;
    
    @Column(nullable = false)
    private String affiliateLink;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id") // name e' nome della colonna, refe...Name e' nome della colonna di tabella Category a cui si fa riferimento
    private Category category;
    
    // lato NON proprietario
    // mappedBy indico lato non proprietario della relazione tra due entita'.
    //   indica quale proprieta' nell'altra entita' mappa questa relazione. Evitiamo ridondanze e Hibernate sa che entita' possiede la relazione.
    //   In questo caso indico che la relazione tra Product e PriceHistory è mappata dalla proprieta' product della classe PriceHistory.
    //   Quindi la tab price_history avra' colonna foreign key che fa riferimento a tabella product.
    //   Nota: è relazione oneToMany - lato many PriceHistory detiene fk a product, lato one Product accede ai dati tramite join
    //         non c'e' colonna che fa riferimento a price_history; la relazione e' gestita in memoria dell'applicazione da Hibernate tramite lista priceHistories.
    //         (se ci fosse colonna riferimento a PriceHistory per ogni relazione, allora impossibile rappresentare relazione OneToMany, prodotto con più prezzi storici,
    //          questo perche' una colonna non puo' contenere molteplici riferimenti in una tabella relazionale)
    // cascade se elimino prodotto elimino automatico rispettive righe in tab priceHistory.
    // orphanRemoval elimina righe che sono orfani di prodotto, ossia che non hanno un prodotto associato
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceHistory> priceHistories = new ArrayList<>();

}
