package com.njdealsandclicks.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.njdealsandclicks.model.Product;


/**
 * Il repository gestisce l'interazione con il database per l'entità Product
 */


public interface ProductRepository extends JpaRepository<Product, Long> {
    /* JpaRepository: Fornisce metodi CRUD predefiniti (es. findAll(), save(), deleteById()) */
}
