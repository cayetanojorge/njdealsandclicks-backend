package com.njdealsandclicks.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

@Component
public class YamlLoaderService {
    
    @Value("${custom.init-directory}")
    private String initDirectory;

    // private final EntityInitializedService entityInitializedService;

    // public YamlLoaderService(EntityInitializedService entityInitializedService) {
    //     this.entityInitializedService = entityInitializedService;
    // }


    public <T> List<T> loadEntitiesFromYaml(String fileName, Class<T> entityType, Function<Map<String, Object>, T> mapper) {
        Yaml yaml = new Yaml();
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initDirectory + "/" + fileName)) {
            if(inputStream == null) {
                throw new RuntimeException("File not found: " + initDirectory + "/" + fileName);
            }

            Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);
            List<Map<String, Object>> entitiesData = data.get(entityType.getSimpleName().toLowerCase());

            return entitiesData != null
                ? entitiesData.stream().map(mapper).collect(Collectors.toList())
                : Collections.emptyList();
        } catch (Exception e) {
            // System.err.println("Errore durante l'inizializzazione del file YAML: " + e.getMessage());
            throw new RuntimeException("Error loading entities from YAML file: " + fileName, e);
        }
    }
}
