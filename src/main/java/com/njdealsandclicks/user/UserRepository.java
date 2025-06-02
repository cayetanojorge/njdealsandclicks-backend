package com.njdealsandclicks.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM category WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);

    /* ok per gran numero di record nel database, poiché la verifica utilizza un'operazione SQL ottimizzata (IN con lista) */
    @Query("SELECT u.publicId FROM User u WHERE u.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);

    // escludere utenti disattivati
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
}
