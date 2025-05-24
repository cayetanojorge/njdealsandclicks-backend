package com.njdealsandclicks.category;

// import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
// import java.util.Collections;
import java.util.List;
import java.util.Map;
// import java.util.function.Function;
// import java.util.stream.Collectors;
import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
// import org.yaml.snakeyaml.Yaml;

import com.njdealsandclicks.common.dbinitializer.EntityInitializer;
import com.njdealsandclicks.entityinitialized.EntityInitializedService;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryInitializer implements EntityInitializer {

    private static final String PREFIX_PUBLIC_ID = "categ_";

    // // // @Value("${custom.init-directory}")
    // // // private String initDirectory;

    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final EntityInitializedService entityInitializedService;
    private final PublicIdGeneratorService publicIdGeneratorService;
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

    @Override
    public String getInitializationVersion() {
        return "1.0"; 
    }
    
    @Override
    @Transactional
    public void initialize() {
        if (!entityInitializedService.needsInitialization(getEntityName(), getYamlName())) {
            // log.info("Skipping initialization for {}", getEntityName());
            return;
        }
        
        List<Category> allCategories = yamlService.loadEntitiesFromYaml(
            getYamlName(),
            Category.class,
            this::mapYamlToCategory
        );

        List<String> existingCategoryNames = categoryRepository.findAllNames();

        // filter sub and parent categories
        allCategories = filterSubCategoriesInitDb(allCategories, existingCategoryNames);
        List<Category> categoriesToSave = filterParentCategoriesInitDb(allCategories, existingCategoryNames);

        // calculate how many plublicIds we need - produce them - assigned them
        int nCategories = getNcategInitDb(categoriesToSave);
        // // // List<String> publicIds = getNPublicIds(nCategories);
        List<String> publicIds = createBatchPublicIdsV2(nCategories);
        Iterator<String> it = publicIds.iterator();
        for(int i=0; i<categoriesToSave.size(); i++) {
            Category category = categoriesToSave.get(i);
            category.setPublicId(it.next());
            if(category.getSubCategories()!=null) {
                for(Category sub : category.getSubCategories()) {
                    sub.setPublicId(it.next());
                }
            }
        }

        // saved parents and map subcategories with their parent's name
        List<Category> parentsToSave = new ArrayList<>();
        Map<String, List<Category>> mapParentSubs = new HashMap<>();
        for(int i=0; i<categoriesToSave.size(); i++) {
            Category category = categoriesToSave.get(i);
            if(category.getSubCategories()!=null) {
                List<Category> subsToSave = new ArrayList<>();
                for(Category sub : category.getSubCategories()) {
                    // mapParentSubs.put(category.getName(), sub);
                    subsToSave.add(sub);
                }
                mapParentSubs.put(category.getName(), subsToSave);
                category.setSubCategories(null);
            }
            parentsToSave.add(category);
        }
        categoryRepository.saveAll(parentsToSave);

        // map get parent's name-subs - then update sub's parentCategory and parent's subCategories after saved of subs
        for(var entry : mapParentSubs.entrySet()) {
            Category parent = categoryService.getCategoryByName(entry.getKey());
            List<Category> subCategories =  entry.getValue();
            for(int i=0; i<subCategories.size(); i++) {
                subCategories.get(i).setParentCategory(parent);
            }
            List<Category> subCategoriesSaved = categoryRepository.saveAll(subCategories);
            parent.setSubCategories(subCategoriesSaved);
            categoryRepository.save(parent);
        }
        
        // categoryRepository.saveAll(categories);
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
    
    private List<Category> filterSubCategoriesInitDb(List<Category> allCategories, List<String> existingCategoryNames) {
        for(int i=0; i<allCategories.size(); i++) {
            Category category = allCategories.get(i);
            if(category.getSubCategories()!=null) {
                List<Category> filteredSubCategories = category.getSubCategories().stream()
                    .filter(sub -> !existingCategoryNames.contains(sub.getName()))
                    .collect(Collectors.toList());
                
                if(filteredSubCategories.isEmpty()) {
                    category.setSubCategories(null);
                }
                else {
                    category.setSubCategories(filteredSubCategories);
                }
            }
        }
        return allCategories;
    }

    private List<Category> filterParentCategoriesInitDb(List<Category> allCategories, List<String> existingCategoryNames) {
        List<Category> categoriesToSave = new ArrayList<>();
        for(int i=0; i<allCategories.size(); i++) {
            // se padre gia' presente in db mi tengo solo le sue sottocategorie
            Category category = allCategories.get(i);
            if(existingCategoryNames.contains(category.getName())) {
                if(category.getSubCategories()!=null) {
                    Category parent = categoryService.getCategoryByName(category.getName());
                    for(Category sub : category.getSubCategories()) {
                        sub.setParentCategory(parent);
                        categoriesToSave.add(sub);
                    }
                }
                continue;
            }
            // padre non presente e lo aggiungo come da salvare, avendo eventuali subcategories
            categoriesToSave.add(category);
        }
        return categoriesToSave;
    }

    private int getNcategInitDb(List<Category> categoriesToSave) {
        int nCategories = categoriesToSave.size();
        for(Category category : categoriesToSave) {
            if(category.getSubCategories()!=null) {
                nCategories = nCategories + category.getSubCategories().size();
            }
        }
        return nCategories;
    }

    private List<String> createBatchPublicIdsV2(int nPublicIds) {
        return publicIdGeneratorService.generateBatchPublicIdsV2(PREFIX_PUBLIC_ID, categoryRepository::filterAvailablePublicIds, nPublicIds);
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