package com.njdealsandclicks.entityinitialized;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class EntityInitialized {
    
    @Id
    private String entityName;
    
    @Column(nullable = false, updatable = true)
    private boolean initialized = false;

    @Column(nullable = true)
    private ZonedDateTime lastInitialized;

    @Column(unique = true)
    private String fileHash;

    private String initializationVersion;
    
    public EntityInitialized(String entityName) {
        this.entityName = entityName;
    }
}