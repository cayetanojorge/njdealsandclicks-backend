package com.njdealsandclicks.newsletter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Newsletter extends BaseEntity{

    // cascade se elimino user elimino rispettive riga in tab newsletter
    // orphanRemoval che se record in tab newsletter è orfano di user allora elimino
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Boolean generalNewsletter = false; // true se iscrizione tutti prodotti, default false


    // quando era possibile solo 1 prodotto specifico
    // // @ManyToOne(optional = true)
    // // @JoinColumn(name = "product_id", referencedColumnName = "id")
    // // private Product product;

    // ora possibilità a più prodotti
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "newsletter_product",
        joinColumns = @JoinColumn(name = "newsletter_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    
    /*
    @ManyToMany con categories:
    Permette a un utente di iscriversi a più categorie.
    Hibernate creerà una tabella di join intermedia chiamata newsletter_product, come specificato con @JoinTable. Questa tabella avrà due colonne: newsletter_id, product_id
    Questa soluzione è scalabile perché supporta l'aggiunta di nuove categorie senza dover modificare la struttura delle tabelle.
    PERSIST: Crea nuovi record nella tabella di join newsletter_category quando aggiungi categorie a una newsletter.
    MERGE: Sincronizza le modifiche a Newsletter con la tabella di join.
    */
    // quando elimino record in newsletter voglio che si elimino record in tabella newsletter_category, 2 modi:
    //   . metodo 1: in service prima di eliminare newsletter, recuperare i record della tab newsletter_category far clear e poi eliminare record in newsletter
    //   . metodo 2: in repository con query personalizzata.
    // se numero record in newsletter_category riferito a certa newsletter è limintato allora meglio metodo 1,
    // se invece abbiamo newsletter a cui associato molte categorie, allora meglio metodo 2, piu' veloce, evita caricare entita' in memoria
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "newsletter_category",
        joinColumns = @JoinColumn(name = "newsletter_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(nullable = true)
    private ZonedDateTime updatedAt;

    
    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
