package com.njdealsandclicks.category;

// import java.io.InputStream;
import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
import java.util.Map;
// import java.util.function.Function;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
// import org.yaml.snakeyaml.Yaml;

import com.njdealsandclicks.common.dbinitializer.EntityInitializer;
import com.njdealsandclicks.entityinitialized.EntityInitializedService;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryInitializer implements EntityInitializer {

    // // // @Value("${custom.init-directory}")
    // // // private String initDirectory;

    private final CategoryRepository categoryRepository;
    private final EntityInitializedService entityInitializedService;
    private final YamlService yamlService;
    // private final InitializationProperties properties;
    
    @Override
    public String getEntityName() { 
        return "category";
    }
    
    @Override
    public String getYamlName() { 
        return "categories.yml";
    }
    
    @Transactional
    public void initialize() {
        if (!entityInitializedService.needsInitialization(getEntityName(), getYamlName())) {
            // log.info("Skipping initialization for {}", getEntityName());
            return;
        }
        
        List<Category> categories = yamlService.loadEntitiesFromYaml(
            getYamlName(),
            Category.class,
            this::mapYamlToCategory
        );
        
        categoryRepository.saveAll(categories);
        entityInitializedService.markAsInitialized(getEntityName(), getYamlName(), getInitializationVersion());
    }

    private Category mapYamlToCategory(Map<String, Object> data) {
        Category category = new Category();
        category.setName((String) data.get("name"));
        category.setDescription((String) data.get("description"));
        category.setImageUrl((String) data.get("imageUrl"));
        category.setSlug((String) data.get("slug"));
        category.setIsActive((Boolean) data.get("isActive"));
        category.setDisplayOrder((Integer) data.get("displayOrder"));

        // se categoria ha subCategories 
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataSubCategories = (List<Map<String, Object>>) data.get("subCategories");
        if(dataSubCategories!=null) {
            List<Category> subCategories = new ArrayList<>();
            for(Map<String, Object> dataSub : dataSubCategories) {
                Category subCategory = new Category();
                subCategory.setName((String) dataSub.get("name"));
                subCategory.setDescription((String) dataSub.get("description"));
                subCategory.setImageUrl((String) dataSub.get("imageUrl"));
                subCategory.setSlug((String) dataSub.get("slug"));
                subCategory.setIsActive((Boolean) dataSub.get("isActive"));
                subCategory.setDisplayOrder((Integer) dataSub.get("displayOrder"));
                subCategories.add(subCategory);
            }
            category.setSubCategories(subCategories);
        }
    
        return category;
    }
    

    // // // public <T> List<T> loadEntitiesFromYaml(String fileName, Class<T> entityType, Function<Map<String, Object>, T> mapper) {
    // // //     Yaml yaml = new Yaml();
    // // //     try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initDirectory + "/" + fileName)) {
    // // //         if(inputStream == null) {
    // // //             throw new RuntimeException("File not found: " + initDirectory + "/" + fileName);
    // // //         }

    // // //         Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);
    // // //         List<Map<String, Object>> entitiesData = data.get(entityType.getSimpleName().toLowerCase());

    // // //         return entitiesData != null
    // // //             ? entitiesData.stream().map(mapper).collect(Collectors.toList())
    // // //             : Collections.emptyList();
    // // //     } catch (Exception e) {
    // // //         // System.err.println("Errore durante l'inizializzazione del file YAML: " + e.getMessage());
    // // //         throw new RuntimeException("Error loading entities from YAML file: " + fileName, e);
    // // //     }
    // // // }
}