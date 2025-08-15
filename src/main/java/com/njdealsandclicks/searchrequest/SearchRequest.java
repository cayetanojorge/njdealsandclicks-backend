package com.njdealsandclicks.searchrequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.njdealsandclicks.common.BaseEntity;
import com.njdealsandclicks.util.enums.Market;
import com.njdealsandclicks.util.enums.SearchRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "search_request",
       indexes = { 
        @Index(name = "idx_search_request_created_at", columnList = "created_at"),
        @Index(name = "idx_search_request_market_created", columnList = "market, created_at"),
        @Index(name = "idx_search_request_market_hash_created", columnList = "market, query_hash, created_at")
    })
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends BaseEntity {

    @NotBlank
    @Size(max = 2000)
    @Column(name = "input_text", nullable = false, length = 2000)
    private String inputText;

    @Column(name = "is_url", nullable = false)
    private boolean isUrl = false;

    @Size(max = 512)
    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SearchRequestStatus status = SearchRequestStatus.NEW;

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
    @NotNull
    @Min(0)
    @Column(name = "results_count", nullable = false)
    private Integer resultsCount = 0;

    @Size(max = 300)
    @Column(name = "path", length = 300)
    private String path;

    @Size(max = 300)
    @Column(name = "referrer", length = 300)
    private String referrer;

    @Size(max = 100)
    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Size(max = 100)
    @Column(name = "accept_language", length = 100)
    private String acceptLanguage;

    @Size(max = 120)
    @Column(name = "device", length = 120)  // parsed da User-Agent (grezzo va bene)
    private String device;

    @NotNull
    @Column(name = "query_hash", nullable = false, length = 64) // sha-256 dell’input normalizzato
    private String queryHash;

    @NotNull
    @Min(1)
    @Column(name = "count", nullable = false)
    private Integer count = 1; // ripetizioni della stessa query in finestra temporale

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "market", nullable = false, length = 4)
    private Market market;

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
