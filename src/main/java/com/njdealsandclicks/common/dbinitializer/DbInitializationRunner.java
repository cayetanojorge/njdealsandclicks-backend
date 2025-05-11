package com.njdealsandclicks.common.dbinitializer;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DbInitializationRunner {
    private final List<EntityInitializer> initializers;
    
    @PostConstruct
    public void initializeAll() {
        initializers.forEach(EntityInitializer::initialize);
    }
}