package com.njdealsandclicks.article;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByPublicId(String publicId);
    Optional<Article> findBySlug(String slug);
    List<Article> findAllByIsDeletedFalseAndIsPublishedTrue();


    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM article WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(List<String> publicIds);
}
