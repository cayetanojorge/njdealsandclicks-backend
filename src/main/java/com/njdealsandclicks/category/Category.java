package com.njdealsandclicks.category;

// import java.util.ArrayList;
// import java.util.List;

// import com.njdealsandclicks.product.Product;

// import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
// import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(indexes = {
    @Index(name = "idx_category_name", columnList = "name")
})
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // utile per recuperare lista prodotto da tabella Category, ogni prodotto aggiunto poi aggiunto a specifica categoria
    // non utile se abbiamo migliaia di prodotto, dati ridondanti.
    // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    // private List<Product> products = new ArrayList<>();
}
