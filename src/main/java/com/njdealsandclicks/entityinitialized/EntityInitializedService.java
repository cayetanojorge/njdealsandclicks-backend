package com.njdealsandclicks.entityinitialized;

import java.io.IOException;

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
    // private final YamlLoaderService yamlLoader;

    public boolean needsInitialization(String entityName, String yamlName) {
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
    public void markAsInitialized(String entityName, String yamlName, String version) {
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

    // // // TODO magari spostarlo ed usarlo da altra classe
    // // public <T> List<T> loadEntitiesFromYaml(String fileName, Class<T> entityType, Function<Map<String, Object>, T> mapper) {
    // //     Yaml yaml = new Yaml();
    // //     try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initDirectory + "/" + fileName)) {
    // //         if(inputStream == null) {
    // //             throw new RuntimeException("File not found: " + initDirectory + "/" + fileName);
    // //         }

    // //         Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);
    // //         List<Map<String, Object>> entitiesData = data.get(entityType.getSimpleName().toLowerCase());

    // //         return entitiesData != null
    // //             ? entitiesData.stream().map(mapper).collect(Collectors.toList())
    // //             : Collections.emptyList();
    // //     } catch (Exception e) {
    // //         // System.err.println("Errore durante l'inizializzazione del file YAML: " + e.getMessage());
    // //         throw new RuntimeException("Error loading entities from YAML file: " + fileName, e);
    // //     }
    // // }
}
