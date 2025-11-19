package com.njdealsandclicks.common;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/*
-> Obiettivo:
- Non includere nelle equals/hashCode:
    . relazioni @ManyToOne, @OneToMany, @ManyToMany (eviti lazy-load e ricorsioni)
    . campi mutabili (nome, descrizione, ecc.)
- Usare solo un identificatore stabile → nel tuo caso: publicId.

-> Cosa succede così:
- onlyExplicitlyIncluded = true → Lombok ignora tutti i campi tranne quelli con @EqualsAndHashCode.Include.
- Metti Include su publicId → tutte le tue entità avranno equals e hashCode basate solo su publicId.
- Niente relazioni prese in mezzo, niente problemi di LAZY, niente cicli.
Importante: assicurati che publicId venga valorizzato sempre (ad esempio nel service)

-> Nelle entita' figlie:
uso @EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
- callSuper = true → dice a Lombok: usa anche l’equals/hashCode della superclasse, quindi prende il publicId da BaseEntity.
- onlyExplicitlyIncluded = true sulla sottoclasse → Lombok non aggiunge altri campi alla equals/hashCode, a meno che tu non ci metta un altro @EqualsAndHashCode.Include (che per ora io eviterei).
Nota:
@Data da solo genererebbe equals/hashCode su tutti i campi.
Ma se metti anche @EqualsAndHashCode(...) sulla stessa classe, Lombok usa quello esplicito e ignora quello implicito di @Data.

-> cosa otteniamo:
- Niente lazy load involontario
    . Non ci finiscono dentro Category, User, PriceHistory, ecc.
    . Confrontare due entità non genera query extra.
- Niente rischio di StackOverflow
    . Nessun campo relazione partecipa, quindi non puoi avere cicli tipo:
      Category -> parentCategory -> subCategories -> parentCategory...
- Identità logica stabile
    . publicId è esattamente ciò che usi nelle API e nei link (/products/prod_xxx/...), ha senso che sia anche l’identità nelle collection Java.
 */
@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Getter
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @Getter
    @Setter
    @EqualsAndHashCode.Include // <— equals/hashCode usano SOLO questo
    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;
    
}
