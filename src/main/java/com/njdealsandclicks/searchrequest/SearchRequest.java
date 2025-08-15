package com.njdealsandclicks.searchrequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "search_request",
       indexes = { 
        @Index(name = "idx_search_request_created_at", columnList = "created_at"),
        @Index(name = "idx_search_req_hash_created", columnList = "query_hash, created_at")
    })
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends BaseEntity {

    @NotBlank
    @Column(name = "input_text", nullable = false, length = 2000)
    private String inputText;

    @Column(name = "is_url", nullable = false)
    private boolean isUrl = false;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "status", length = 50)
    private String status; // es. "NEW", "PARSED", ...

    // SearchRequest.java (aggiunte)
    /*
    Il campo results_count ti serve per memorizzare quanti prodotti sono stati trovati nella ricerca dell’utente, così puoi distinguere tra:
    . ricerche che non hanno restituito nessun prodotto (results_count = 0) → utili per capire cosa manca nel tuo catalogo e magari dare priorità all’aggiunta.
    . ricerche che hanno restituito qualche prodotto (results_count > 0) → possono comunque essere analizzate, ad esempio per capire se i risultati sono pertinenti o se servono più opzioni.
    In pratica è un indicatore statistico:
    . Marketing → vedi quali query hanno zero risultati e puoi usarle per campagne (“Ora disponibile…”).
    . UX → capisci se la ricerca restituisce abbastanza scelta (es. sempre 1-2 risultati = catalogo troppo scarno).
    . Analisi dati → puoi filtrare in DB WHERE results_count = 0 per avere subito l’elenco delle richieste “vuote” e capire dove agire.
     */
    @Column(name = "results_count")
    private Integer resultsCount;

    @Column(name = "path", length = 300)
    private String path;

    @Column(name = "referrer", length = 300)
    private String referrer;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "accept_language", length = 100)
    private String acceptLanguage;

    @Column(name = "device", length = 120)  // parsed da User-Agent (grezzo va bene)
    private String device;

    @Column(name = "query_hash", length = 64) // sha-256 dell’input normalizzato
    private String queryHash;

    @Column(name = "count")
    private Integer count = 1; // ripetizioni della stessa query in finestra temporale


    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = true)
    private ZonedDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
    }
    
}
