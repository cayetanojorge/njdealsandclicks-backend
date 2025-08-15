package com.njdealsandclicks.searchrequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SearchRequestRepository extends JpaRepository<SearchRequest, UUID> {

    Optional<SearchRequest> findTopByQueryHashAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(String queryHash, ZonedDateTime since);

    /* Data una lista :publicIds, restituisci solo quelli che non sono presenti nella tabella product */
    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM search_request WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(@Param("publicIds") List<String> publicIds);

}