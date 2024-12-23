package com.njdealsandclicks.pricehistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    /* JpaRepository: Fornisce metodi CRUD predefiniti (es. findAll(), save(), deleteById()) */

    /* JPA mi crea automatico questa query grazie a parola chiave 'findBy' e poi 'ProductId' entità per filtrare */
    List<PriceHistory> findByProductId(Long productId);
    
}
