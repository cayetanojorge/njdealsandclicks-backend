package com.njdealsandclicks.pricehistory;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, UUID> {
    /* JpaRepository: Fornisce metodi CRUD predefiniti (es. findAll(), save(), deleteById()) */

    /* JPA mi crea automatico questa query grazie a parola chiave 'findBy' e poi 'ProductId' entità per filtrare */
    List<PriceHistory> findByProductId(UUID productId);
    
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.publicId = :productPublicId ORDER BY ph.recordedAt DESC")
    List<PriceHistory> findByProductPublicId(@Param("productPublicId") String productPublicId);

    @Modifying // Indica che questa è un'operazione di modifica (non solo lettura).
    @Transactional // Garantisce che l'operazione sia eseguita in una transazione.
    @Query("DELETE FROM PriceHistory ph WHERE ph.product.publicId = :productPublicId")
    void deleteByProductPublicId(@Param("productPublicId") String productPublicId);
}
