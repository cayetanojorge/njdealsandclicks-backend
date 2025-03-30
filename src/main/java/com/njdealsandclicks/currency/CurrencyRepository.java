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

    @Query("SELECT c.publicId FROM Currency c WHERE c.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);

    Optional<Currency> findByCode(String code);
}
