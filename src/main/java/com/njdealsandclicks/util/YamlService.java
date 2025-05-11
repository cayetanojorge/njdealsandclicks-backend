package com.njdealsandclicks.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.yaml.snakeyaml.Yaml;

// import com.njdealsandclicks.entityinitialized.EntityInitializedService;

@Component
public class YamlService {

    private final PathUtil pathUtil;

    
    public YamlService(PathUtil pathUtil) {
        this.pathUtil = pathUtil;
    }

    // // // private final EntityInitializedService entityInitializedService;

    // // // public YamlLoaderService(EntityInitializedService entityInitializedService) {
    // // //     this.entityInitializedService = entityInitializedService;
    // // // }

    public String calculateYamlHash(String yamlName) throws IOException {
        String yamlPath = pathUtil.getInitDirectory() + "/" + yamlName;
        Resource resource = new ClassPathResource(yamlPath);
        try (InputStream input = resource.getInputStream()) {
            return DigestUtils.md5DigestAsHex(input);
        }
    }

    public <T> List<T> loadEntitiesFromYaml(String fileName, Class<T> entityType, Function<Map<String, Object>, T> mapper) {
        Yaml yaml = new Yaml();
        String yamlPath = pathUtil.getInitDirectory() + "/" + fileName;
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(yamlPath)) {
            if(inputStream == null) {
                throw new RuntimeException("File not found: " + yamlPath);
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
