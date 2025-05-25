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

    //     @Query(
    // value = """
    //     SELECT unnest(:ids) 
    //     EXCEPT 
    //     SELECT public_id FROM product WHERE public_id IN (:ids)
    //     """,
    // nativeQuery = true
    // )
    // List<String> findAvailablePublicIds(@Param("ids") List<String> ids);


    /* Data una lista :publicIds, restituisci solo quelli che non sono presenti nella tabella product */
    @Query(
        value = 
            """
            SELECT unnest(:publicIds) 
            EXCEPT 
            SELECT publicId FROM Product p WHERE publicId IN :publicIds
            """,
        nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);
    
    /* data lista di publicIds voglio restituire lista di products presenti in db */
    @Query("SELECT p FROM Product p WHERE p.publicId IN :publicIds")
    List<Product> findByPublicIds(@Param("publicIds") List<String> publicIds);

    @Query("SELECT p FROM Product p WHERE p.category.publicId = :categoryPublicId")
    List<Product> findByCategoryPublicId(@Param("categoryPublicId") String categoryPublicId);


    /* per le query sui tags e features che sono json in db postgresql */
    // // // Ricerca prodotti che contengono un tag specifico nel JSONB
    // // // Richiede index GIN per efficienza con molti dati
    // // @Query(value = "SELECT * FROM product WHERE tags @> CAST(:tag AS jsonb)", nativeQuery = true)
    // // List<Product> findByTag(String tag);
    // // // in service
    // // List<Product> products = productRepository.findByTag("[\"oled\"]");
    // // // Oppure, puoi dinamizzare il valore in controller o service:
    // // String tag = objectMapper.writeValueAsString(List.of("oled")); // => '["oled"]'
    // // // ---------
    // // // Future: ricerca per JSONB tag
    // // @Query(value = "SELECT * FROM product WHERE tags @> CAST(:tag AS jsonb)", nativeQuery = true)
    // // List<Product> findByTag(String tag);

    
}
