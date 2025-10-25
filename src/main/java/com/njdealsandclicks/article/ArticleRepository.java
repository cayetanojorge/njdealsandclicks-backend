package com.njdealsandclicks.article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.njdealsandclicks.product.Product;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByPublicId(String publicId);
    Article findBySlug(String slug);
    List<Article> findAllByIsDeletedFalseAndIsPublishedTrue();

    // per evitare problema di LazyInitializationException(=prendo oggetto article ma non carica in memoria lista product perche' relazione many quindi e' lazy)
    @Query("""
        SELECT DISTINCT a
        FROM Article a
        LEFT JOIN FETCH a.products p
        LEFT JOIN FETCH p.category
        WHERE a.isDeleted = false
            AND a.isPublished = true
            AND a.slug = :slug
    """)
    Article findWithProductsBySlug(@Param("slug") String slug);

    @Query("""
        SELECT DISTINCT p
        FROM Article a
        JOIN a.products p
        JOIN ProductMarket pm ON pm.product = p
        JOIN pm.country c
        WHERE a.slug = :slug
        AND a.isDeleted = false
        AND a.isPublished = true
        AND c.code = :countryCode
    """)
    List<Product> findProductsBySlugAndCountry(@Param("slug") String slug,
                                        @Param("countryCode") String countryCode);

    @Query("""
        SELECT a
        FROM Article a
        JOIN a.products p
        WHERE p.publicId = :productPublicId
        AND a.isDeleted = false
        AND a.isPublished = true
    """)
    List<Article> findPublishedArticlesByProductPublicId(@Param("productPublicId") String productPublicId); // per mostrare in pag product details gli articoli dove menziono product

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM article WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(List<String> publicIds);


    /*
    LEFT JOIN article_product → accedi ai prodotti associati all'articolo.
    LEFT JOIN product → per ottenere i product.category_id.
    LEFT JOIN category → per confrontare c.name = :categoryName.
    DISTINCT → necessario per evitare duplicati nel caso un articolo abbia più prodotti con stessa categoria.

    In futuro potresti anche aggiungere un campo main_category_name direttamente sull’articolo (calcolato al momento della creazione o aggiornamento),
    così eviti queste join complesse — ma per ora la tua soluzione è scalabile e corretta.
     */
    @Query(value = """
        SELECT DISTINCT a.*
        FROM article a
        LEFT JOIN article_product ap ON a.id = ap.article_id
        LEFT JOIN product p ON ap.product_id = p.id
        LEFT JOIN category c ON p.category_id = c.id
        WHERE a.is_published = true
        AND a.is_deleted = false
        AND a.public_id <> :excludePublicId
        AND (
            EXISTS (
                SELECT 1
                FROM jsonb_array_elements_text(a.tags) AS t(value)
                WHERE t.value IN (:tags)
            )
            OR c.name = :categoryName
        )
        ORDER BY a.updated_at DESC NULLS LAST
        LIMIT :limit
        """, nativeQuery = true)
    List<Article> findRelatedArticles(
        @Param("excludePublicId") String excludePublicId,
        @Param("tags") List<String> tags,
        @Param("categoryName") String categoryName,
        @Param("limit") int limit
    );

}
