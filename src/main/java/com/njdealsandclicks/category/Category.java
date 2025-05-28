package com.njdealsandclicks.category;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import com.njdealsandclicks.common.BaseEntity;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
    name = "category",
    indexes = {
        @Index(name = "idx_category_public_id", columnList = "public_id"),
        @Index(name = "idx_category_name", columnList = "name")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @NotBlank
    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = true)
    private Integer displayOrder; // per ordinare le categorie in una lista o un menu

    /*
    Vantaggi:
    - Struttura logica: Le categorie figlie possono essere facilmente associate a una categoria principale.
    - Navigazione utente: Gli utenti possono esplorare le categorie partendo da un livello alto e scendendo a livelli più specifici.
    - Organizzazione backend: Utile per organizzare i prodotti e facilitare la gestione da parte degli amministratori.
     */
    @ManyToOne(fetch = FetchType.EAGER) // carica entità categoria con anche la categoria padre, senza query in secondo momento
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    // FetchType.LAZY per evitare caricamenti non necessari delle sottocategorie, Hibernate carica dopo con altra query quando richiesta
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> subCategories;


    // future-todo
    // // // @Column(nullable = false)
    // // // private Boolean createdByUser;
    // // // @Column(nullable = true)
    // // // private User user;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;

    // utile per recuperare lista prodotto da tabella Category, ogni prodotto aggiunto poi aggiunto a specifica categoria
    // non utile se abbiamo migliaia di prodotto, dati ridondanti.
    // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    // private List<Product> products = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
}
