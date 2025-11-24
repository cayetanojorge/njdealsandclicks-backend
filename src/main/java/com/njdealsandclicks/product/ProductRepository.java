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

    @Query("""
        SELECT DISTINCT p
        FROM Product p
            JOIN p.productMarkets pm
            JOIN pm.country c
        WHERE c.code = :countryCode AND pm.isDeleted = false
    """)
    List<Product> findByCountryCode(@Param("countryCode") String countryCode);

    // x filtrare i prodotti collegati a un articolo in base al market
    @Query("SELECT p FROM Product p WHERE p.publicId IN :publicIds AND p.country.code = :countryCode")
    List<Product> findByPublicIdsAndCountry(@Param("publicIds") List<String> publicIds,
                                            @Param("countryCode") String countryCode);

    /* ok per gran numero di record nel database, poiché la verifica utilizza un'operazione SQL ottimizzata (IN con lista) */
    @Query("SELECT p.publicId FROM Product p WHERE p.publicId IN :publicIds")
    List<String> findByPublicIdsAndCountryfindExistingPublicIds(@Param("publicIds") List<String> publicIds);

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
    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM product WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);
    
    /* data lista di publicIds voglio restituire lista di products presenti in db */
    @Query("SELECT p FROM Product p WHERE p.publicId IN :publicIds")
    List<Product> findByPublicIds(@Param("publicIds") List<String> publicIds);

    @Query("SELECT p FROM Product p WHERE p.category.publicId = :categoryPublicId")
    List<Product> findByCategoryPublicId(@Param("categoryPublicId") String categoryPublicId);

    @Query(value = """
        SELECT p.*
        FROM product p
        JOIN country co ON p.country_id = co.id
        JOIN category c ON p.category_id = c.id
        WHERE co.code = :countryCode
        AND p.public_id NOT IN (:excludeIds)
        AND (
                ( :hasTags = TRUE AND EXISTS (
                    SELECT 1
                    FROM jsonb_array_elements_text(p.tags) t
                    WHERE t IN (:tags)
                ))
            OR ( :categoryName IS NOT NULL AND c.name = :categoryName )
            )
        ORDER BY p.review_count DESC NULLS LAST, p.rating DESC NULLS LAST
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> findRelatedProductsByCountry(
        @Param("excludeIds") List<String> excludeIds,
        @Param("tags") List<String> tags,
        @Param("hasTags") boolean hasTags,
        @Param("categoryName") String categoryName,
        @Param("countryCode") String countryCode,
        @Param("limit") int limit
    );

    // --- search per filtrare per paese ---
    @Query(value = """
        SELECT p.*
        FROM product p
        JOIN country co ON p.country_id = co.id
        LEFT JOIN category c ON p.category_id = c.id
        WHERE co.code = :countryCode
        AND (
            p.name ILIKE CONCAT('%', :q, '%')
            OR (p.brand IS NOT NULL AND p.brand ILIKE CONCAT('%', :q, '%'))
            OR (p.description IS NOT NULL AND p.description ILIKE CONCAT('%', :q, '%'))
            OR EXISTS (
                SELECT 1
                FROM jsonb_array_elements_text(p.tags) t
                WHERE t ILIKE CONCAT('%', :q, '%')
            )
        )
        ORDER BY p.review_count DESC NULLS LAST, p.rating DESC NULLS LAST
        LIMIT :limit
        """, nativeQuery = true)
    List<Product> searchByTextAndCountry(@Param("q") String q,
                                        @Param("limit") int limit,
                                        @Param("countryCode") String countryCode);

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
