package com.njdealsandclicks.newsletter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsletterRepository extends JpaRepository<Newsletter, UUID> {
    Optional<Newsletter> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    /* controllo publicId presente in batch se gia' presente in db  */
    @Query("SELECT n.publicId FROM Newsletter n WHERE n.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);
    
    /* per trovare record a seconda del publicId del user. Ogni record corrisponde a singolo utente */
    @Query("SELECT n FROM Newsletter n WHERE n.user.publicId = :userPublicId")
    Optional<Newsletter> findByUserPublicId(@Param("userPublicId") String userPublicId);

    @Query("SELECT n FROM Newsletter n WHERE n.user.email = :userEmail")
    Optional<Newsletter> findByUserEmail(@Param("userEmail") String userEmail);

    // metodo 2 delete
    // @Modifying
    // @Query("DELETE FROM newsletter_category WHERE newsletter_id = :newsletterId")
    // void deleteNewsletterCategories(@Param("newsletterId") Long newsletterId);
}
