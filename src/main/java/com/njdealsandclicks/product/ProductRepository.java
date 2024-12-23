package com.njdealsandclicks.product;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Il repository gestisce l'interazione con il database per l'entità Product
 */


public interface ProductRepository extends JpaRepository<Product, Long> {
    /* JpaRepository: Fornisce metodi CRUD predefiniti (es. findAll(), save(), deleteById()) */
    List<Product> findByCategoryId(Long id);
}
