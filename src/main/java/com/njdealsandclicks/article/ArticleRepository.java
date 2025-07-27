package com.njdealsandclicks.article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByPublicId(String publicId);
    Article findBySlug(String slug);
    List<Article> findAllByIsDeletedFalseAndIsPublishedTrue();

    @Query("""
        SELECT a
        FROM Article a
        JOIN a.products p
        WHERE p.publicId = :productPublicId
        AND a.isDeleted = false
        AND a.isPublished = true
    """)
    List<Article> findPublishedArticlesByProductPublicId(String productPublicId); // per mostrare in pag product details gli articoli dove menziono product

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
        AND a.public_id != :excludePublicId
        AND (
            EXISTS (
            SELECT 1 FROM unnest(:tags) AS tag
            WHERE tag = ANY(a.tags)
            )
            OR c.name = :categoryName
        )
        ORDER BY a.updated_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Article> findRelatedArticles(
        String excludePublicId,
        List<String> tags,
        String categoryName,
        int limit
    );

}
