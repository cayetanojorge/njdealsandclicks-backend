package com.njdealsandclicks.productmarket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.njdealsandclicks.product.Product;


public interface ProductMarketRepository extends JpaRepository<ProductMarket, UUID> {
    
    Optional<ProductMarket> findByPublicId(String publicId);
    Optional<ProductMarket> findByProduct_PublicIdAndCountry_Code(String productPublicId, String countryCode);
    List<ProductMarket> findAllByProduct_PublicId(String productPublicId);
    List<ProductMarket> findAllByCountry_CodeOrderByCurrentPriceAsc(String countryCode);


    @Query("SELECT p FROM ProductMarket p WHERE p.publicId IN :publicIds")
    List<ProductMarket> findByPublicIds(@Param("publicIds") List<String> publicIds);

    @Query("""
        SELECT pm
        FROM ProductMarket pm
        JOIN FETCH pm.product p
        JOIN FETCH pm.country c
        WHERE p.publicId IN :productPublicIds
        AND c.code = :countryCode
    """)
    List<ProductMarket> findAllByProductPublicIdsAndCountry(List<String> productPublicIds, String countryCode);


    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM product_market WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);


    // x filtrare i prodotti collegati a un articolo in base al market
    @Query("SELECT pm.product FROM ProductMarket pm WHERE pm.country.code = :countryCode AND pm.product.publicId IN :publicIds")
    List<Product> findByPublicIdsAndCountry(@Param("publicIds") List<String> publicIds,
                                            @Param("countryCode") String countryCode);

    @Query(value = """
        SELECT pm.*
        FROM product_market pm
        JOIN country co ON pm.country_id = co.id
        JOIN product p ON pm.product_id = p.id
        LEFT JOIN category c ON p.category_id = c.id
        WHERE co.code = :countryCode
        AND p.public_id NOT IN (:excludeIds)
        AND (
                (:hasTags = TRUE AND EXISTS (
                    SELECT 1
                    FROM jsonb_array_elements_text(p.tags) t
                    WHERE t IN (:tags)
                ))
            OR (:categoryName IS NOT NULL AND c.name = :categoryName)
            OR (:hasTags = FALSE AND :categoryName IS NULL)            -- Fallback: se non ho né tag né categoria, non filtrare
        )
        ORDER BY pm.review_count DESC NULLS LAST, pm.rating DESC NULLS LAST
        LIMIT :limit
        """, nativeQuery = true)
    List<ProductMarket> findRelatedProductsByCountry(
        @Param("excludeIds") List<String> excludeIds,
        @Param("tags") List<String> tags,
        @Param("hasTags") boolean hasTags,
        @Param("categoryName") String categoryName,
        @Param("countryCode") String countryCode,
        @Param("limit") int limit
    );

    // --- search per filtrare per paese ---
    @Query(value = """
        SELECT pm.*
        FROM product_market pm
        JOIN country co ON pm.country_id = co.id
        JOIN product p  ON pm.product_id = p.id
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
        ORDER BY pm.review_count DESC NULLS LAST, pm.rating DESC NULLS LAST
        LIMIT :limit
        """, nativeQuery = true)
    List<ProductMarket> searchByTextAndCountry(@Param("q") String q,
                                        @Param("limit") int limit,
                                        @Param("countryCode") String countryCode);
}
