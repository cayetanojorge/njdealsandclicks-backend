package com.njdealsandclicks.currency;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CurrencyRepository extends JpaRepository<Currency, UUID> {
    Optional<Currency> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM category WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(List<String> publicIds);

    @Query("SELECT c.publicId FROM Currency c WHERE c.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);

    Optional<Currency> findByCode(String code);
}
