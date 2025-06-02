package com.njdealsandclicks.newsletter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsletterRepository extends JpaRepository<Newsletter, UUID> {
    Optional<Newsletter> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM category WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);

    /* controllo publicId presente in batch se gia' presente in db  */
    @Query("SELECT n.publicId FROM Newsletter n WHERE n.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);
    
    /* per trovare record a seconda del publicId del user. Ogni record corrisponde a singolo utente */
    @Query("SELECT n FROM Newsletter n WHERE n.user.publicId = :userPublicId")
    Optional<Newsletter> findByUserPublicId(@Param("userPublicId") String userPublicId);

    @Query("SELECT n FROM Newsletter n WHERE n.user.email = :userEmail")
    Optional<Newsletter> findByUserEmail(@Param("userEmail") String userEmail);



    /* --- DELETE --- */
    /* quando elimino newsletter e abbiamo molti product e category per newsletter meglio procedere con le query */
    @Modifying
    @Query(value = "DELETE FROM newsletter_category WHERE newsletter_id = :id", nativeQuery = true)
    int deleteNewsletterCategories(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM newsletter_product WHERE newsletter_id = :id", nativeQuery = true)
    int deleteNewsletterProducts(@Param("id") UUID id);
}
