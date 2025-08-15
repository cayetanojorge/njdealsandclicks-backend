package com.njdealsandclicks.searchrequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.njdealsandclicks.util.enums.Market;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, UUID> {


    /* Data una lista :publicIds, restituisci solo quelli che non sono presenti nella tabella search_products */
    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM search_request WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);


    /* Dedup: prendi l'ultima richiesta per (market, queryHash) in una certa finestra temporale */
    @Query("""
        SELECT sr
        FROM SearchRequest sr
        WHERE sr.market = :market
          AND sr.queryHash = :queryHash
          AND sr.createdAt >= :since
        ORDER BY sr.createdAt DESC
    """)
    Optional<SearchRequest> findLatestByMarketAndQueryHashSince(@Param("market") Market market,
                                                                @Param("queryHash") String queryHash,
                                                                @Param("since") ZonedDateTime since);


    /* ------- x analytics ------- */

    // Tutte le ricerche senza risultati
    List<SearchRequest> findByResultsCount(int resultsCount);

    // Le ultime ricerche per mercato
    List<SearchRequest> findTop20ByMarketOrderByCreatedAtDesc(Market market);

    // Conteggio ricerche per giorno
    @Query("""
        SELECT DATE(sr.createdAt), COUNT(sr)
        FROM SearchRequest sr
        GROUP BY DATE(sr.createdAt)
        ORDER BY DATE(sr.createdAt) DESC
    """)
    List<Object[]> countSearchesPerDay();


}