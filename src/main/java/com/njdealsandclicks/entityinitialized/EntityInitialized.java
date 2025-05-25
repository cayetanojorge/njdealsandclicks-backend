package com.njdealsandclicks.entityinitialized;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "entity_initialized")
@Getter
@Setter
@NoArgsConstructor
public class EntityInitialized {
    
    @Id
    @Column(name = "entity_name")
    private String entityName;
    
    @Column(name = "initialized", nullable = false, updatable = true)
    private boolean initialized = false;

    @Column(name = "last_initialized", nullable = true)
    private ZonedDateTime lastInitialized;

    @Column(name = "file_hash", unique = true)
    private String fileHash;

    @Column(name = "initialization_version")
    private String initializationVersion;
    
    public EntityInitialized(String entityName) {
        this.entityName = entityName;
    }
}