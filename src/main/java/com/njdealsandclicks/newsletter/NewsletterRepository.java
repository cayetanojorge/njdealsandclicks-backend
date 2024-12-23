package com.njdealsandclicks.newsletter;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    // Newsletter findByUserEmail(String email); // Trovare newsletter tramite email
    Optional<Newsletter> findByUserEmail(String email); // Trovare newsletter tramite email

    // metodo 2
    // @Modifying
    // @Query("DELETE FROM newsletter_category WHERE newsletter_id = :newsletterId")
    // void deleteNewsletterCategories(@Param("newsletterId") Long newsletterId);
}
