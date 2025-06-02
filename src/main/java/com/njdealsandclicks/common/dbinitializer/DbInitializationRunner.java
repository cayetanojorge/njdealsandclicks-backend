package com.njdealsandclicks.common.dbinitializer;

import java.util.Comparator;
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
        //initializers.forEach(EntityInitializer::initialize); // non garantisce che currency avvenga prima di country
        initializers.stream()
        .sorted(Comparator.comparing(EntityInitializer::getExecutionOrder))
        .forEach(EntityInitializer::initialize);
    }
}