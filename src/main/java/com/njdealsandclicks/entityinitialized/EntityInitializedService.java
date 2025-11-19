package com.njdealsandclicks.entityinitialized;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.njdealsandclicks.common.dbinitializer.DbInitializationProperties;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntityInitializedService {

    private final EntityInitializedRepository entityInitializedRepository;
    private final YamlService yamlService;
    private final DbInitializationProperties properties;
    private final DateUtil dateUtil;

    public boolean needsInitialization(@NonNull String entityName, String yamlName) {
        if (properties.isForce() || properties.getForceEntities().contains(entityName)) {
            return true;
        }
        
        EntityInitialized entityInitialized = entityInitializedRepository.findById(entityName).orElse(new EntityInitialized(entityName));
            
        if (entityInitialized.isInitialized()) {
            try {
                return !yamlService.calculateYamlHash(yamlName).equals(entityInitialized.getFileHash());
            } catch (IOException e) {
                // log.warn("Failed to calculate YAML hash, forcing initialization", e);
                return true;
            }
        }
        return true;
    }
    
    @Transactional
    public void markAsInitialized(@NonNull String entityName, String yamlName, String version) {
        try {            
            EntityInitialized entityInitialized = entityInitializedRepository.findById(entityName)
                .orElse(new EntityInitialized(entityName));
            entityInitialized.setInitialized(true);
            entityInitialized.setLastInitialized(dateUtil.getCurrentDateTime());
            entityInitialized.setFileHash(yamlService.calculateYamlHash(yamlName));
            entityInitialized.setInitializationVersion(version);
            entityInitializedRepository.save(entityInitialized);
        } catch (IOException e) {
            throw new RuntimeException("Failed to calculate YAML hash", e);
        }
    }
    
}
