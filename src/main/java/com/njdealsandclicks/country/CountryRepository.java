package com.njdealsandclicks.country;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository extends JpaRepository<Country, UUID> {
    
    Optional<Country> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);
    Optional<Country> findByCode(String code);

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM category WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(List<String> publicIds);
}
