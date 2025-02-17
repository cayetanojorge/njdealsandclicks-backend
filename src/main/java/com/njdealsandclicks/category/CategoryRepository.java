package com.njdealsandclicks.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByPublicId(String publicId);
    boolean existsByPublicId(String publicId);

    /* ok per gran numero di record nel database, poiché la verifica utilizza un'operazione SQL ottimizzata (IN con lista) */
    @Query("SELECT c.publicId FROM Category c WHERE c.publicId IN :publicIds")
    List<String> findExistingPublicIds(@Param("publicIds") List<String> publicIds);
    
    /* data lista di publicIds voglio restituire lista di category presenti in db */
    @Query("SELECT c FROM Category c WHERE c.publicId IN :publicIds")
    List<Category> findByPublicIds(@Param("publicIds") List<String> publicIds);

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT c.name FROM Category c")
    List<String> findAllNames();

    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findAllActiveCategoriesOrderByDisplayOrder();

    /*
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Optional<Category> findByIdWithSubCategories(@Param("id") UUID id);
     */
    // grazie a JOIN FETCH, carica la categoria e le sue sottocategorie. Non ci sono roundtrip extra al database quando accedi a subCategories.
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.publicId = :publicId")
    Optional<Category> findByPublicIdWithSubCategories(@Param("publicId") String publicId);

}
