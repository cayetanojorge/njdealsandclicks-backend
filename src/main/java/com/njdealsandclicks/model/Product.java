package com.njdealsandclicks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


/**
 * Definisci l'entità Product che rappresenta una tabella nel database.
 */


@Entity /* per indicate che sia tabella in db */
@Data /* @ToString, @EqualsAndHashCode, @Getter / @Setter and @RequiredArgsConstructor */
public class Product {

    @Id /* indica chiave primaria */
    @GeneratedValue(strategy =  GenerationType.IDENTITY) /* indica generazione automatica */
    private Long id;

    private String name;
    private String description;
    private double price;    
}
