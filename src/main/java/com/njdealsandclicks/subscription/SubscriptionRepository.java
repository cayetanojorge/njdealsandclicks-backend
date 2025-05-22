package com.njdealsandclicks.subscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    @Query(value = """
        SELECT elem AS available_public_id
        FROM unnest(CAST(ARRAY[?1] AS text[])) AS elem
        WHERE NOT EXISTS (
            SELECT 1 FROM category WHERE public_id = elem
        )
        """, nativeQuery = true)
    List<String> filterAvailablePublicIds(List<String> publicIds);

    boolean existsByPlanName(String planName);
    
    @Query("SELECT s.planName FROM Subscription s WHERE s.planName IN :planNames")
    List<String> findPlanNamesNotIn(@Param("planNames") List<String> planNames);

    @Query("SELECT s.planName FROM Subscription s")
    List<String> findAllPlanNames();

    /* ok per gran numero di record nel database, poiché la verifica utilizza un'operazione SQL ottimizzata (IN con lista) */
    @Query("SELECT s.publicId FROM Subscription s WHERE s.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);

    Optional<Subscription> findByPlanName(String planName);
}