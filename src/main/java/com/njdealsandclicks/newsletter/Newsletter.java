package com.njdealsandclicks.newsletter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "newsletter")
@Data
@EqualsAndHashCode(callSuper = true)
public class Newsletter extends BaseEntity{

    @OnDelete(action = OnDeleteAction.CASCADE) // se elimino utente con tool esterni a JPA (fastapi o manuale a db) elimino newsletter
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    @NotNull
    @Column(name = "general_newsletter", nullable = false)
    private Boolean generalNewsletter = false; // true se iscrizione tutti prodotti, default false

    // ora possibilità a più prodotti - creera' una tabella newsletter_product(newsletter_id, product_id)
    @ManyToMany
    @JoinTable(
        name = "newsletter_product",
        joinColumns = @JoinColumn(name = "newsletter_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;
    
    /*
    @ManyToMany con categories:
    Permette a un utente di iscriversi a più categorie.
    Hibernate creerà una tabella di join intermedia chiamata newsletter_product, come specificato con @JoinTable.
    Questa tabella avrà due colonne: newsletter_id, product_id
    Questa soluzione è scalabile perché supporta l'aggiunta di nuove categorie senza dover modificare la struttura delle tabelle.
    */
    // quando elimino record in newsletter voglio che si elimino record in tabella newsletter_category, 2 modi:
    //   . metodo 1: in service prima di eliminare newsletter, recuperare i record della tab newsletter_category far clear e poi eliminare record in newsletter
    //   . metodo 2: in repository con query personalizzata.
    // se numero record in newsletter_category riferito a certa newsletter è limintato allora meglio metodo 1,
    // se invece abbiamo newsletter a cui associato molte categorie, allora meglio metodo 2, piu' veloce, evita caricare entita' in memoria
    @ManyToMany
    @JoinTable(
        name = "newsletter_category",
        joinColumns = @JoinColumn(name = "newsletter_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;

    
    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }

}
