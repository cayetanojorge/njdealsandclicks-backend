package com.njdealsandclicks.product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * Il repository gestisce l'interazione con il database per l'entità Product
 */


public interface ProductRepository extends JpaRepository<Product, UUID> {
    /* JpaRepository: Fornisce metodi CRUD predefiniti (es. findAll(), save(), deleteById()) */

    Optional<Product> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);
    boolean existsByAffiliateLink(String affiliateLink);

    /* ok per gran numero di record nel database, poiché la verifica utilizza un'operazione SQL ottimizzata (IN con lista) */
    @Query("SELECT p.publicId FROM Product p WHERE p.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);
    
    /* data lista di publicIds voglio restituire lista di products presenti in db */
    @Query("SELECT p FROM Product p WHERE p.publicId IN :publicIds")
    List<Product> findByPublicIds(@Param("publicIds") List<String> publicIds);

    @Query("SELECT p FROM Product p WHERE p.category.publicId = :categoryPublicId")
    List<Product> findByCategoryPublicId(@Param("categoryPublicId") String categoryPublicId);
}
