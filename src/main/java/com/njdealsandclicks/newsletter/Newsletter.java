package com.njdealsandclicks.newsletter;

import java.util.List;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Newsletter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // cascade se elimino user elimino rispettive riga in tab newsletter
    // orphanRemoval che se record in tab newsletter è orfano di user allora elimino
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private boolean generalSubsription = false; // true se iscrizione tutti prodotti, default false

    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    /*
    @ManyToMany con categories:
    Permette a un utente di iscriversi a più categorie.
    Viene creata una tabella in db con join newsletter_category con due colonne fk che riferiscono a Newsletter e Category.
    Questa soluzione è scalabile perché supporta l'aggiunta di nuove categorie senza dover modificare la struttura delle tabelle.
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

}
