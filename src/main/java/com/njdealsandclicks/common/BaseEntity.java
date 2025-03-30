package com.njdealsandclicks.common;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class BaseEntity {

    @Getter
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true, updatable = false)
    private String publicId;
    
}
