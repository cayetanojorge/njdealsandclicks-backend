package com.njdealsandclicks.entityinitialized;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityInitializedRepository extends JpaRepository<EntityInitialized, String> {
    
}
