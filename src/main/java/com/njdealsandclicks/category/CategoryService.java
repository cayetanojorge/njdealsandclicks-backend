package com.njdealsandclicks.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.category.CategoryCreateUpdateDTO;
import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.util.DatabaseInitializationService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

import jakarta.annotation.PostConstruct;

@Service
public class CategoryService {

    // // // private static final int MAX_ATTEMPTS = 3; // n massimo di tentativi di batch per generare publicId
    private static final String PREFIX_PUBLIC_ID = "categ_";

    private final CategoryRepository categoryRepository;
    private final DatabaseInitializationService databaseInitializationService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;
    

    public CategoryService(CategoryRepository categoryRepository, DatabaseInitializationService databaseInitializationService, 
                            PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.categoryRepository = categoryRepository;
        this.databaseInitializationService = databaseInitializationService;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    @PostConstruct
    private void initializeCategories() {
        List<Category> allCategories = databaseInitializationService.loadEntitiesFromYaml(
            "categories.yml",
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
            Category parent = getCategoryByName(entry.getKey());
            List<Category> subCategories =  entry.getValue();
            for(int i=0; i<subCategories.size(); i++) {
                subCategories.get(i).setParentCategory(parent);
            }
            List<Category> subCategoriesSaved = categoryRepository.saveAll(subCategories);
            parent.setSubCategories(subCategoriesSaved);
            categoryRepository.save(parent);
        }
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
                    Category parent = getCategoryByName(category.getName());
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

    // // // private List<String> getNPublicIds(int nPublicIds) {
    // // //     List<String> retNpublicIds = new ArrayList<>();
    // // //     while(retNpublicIds.size()<nPublicIds) {
    // // //         List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID, nPublicIds);
    // // //         List<String> existingIds = categoryRepository.findExistingPublicIds(publicIdBatch);
    // // //         List<String> uniqueIds = publicIdBatch.stream()
    // // //                                                   .filter(id -> !existingIds.contains(id))
    // // //                                                   .collect(Collectors.toList());
    // // //         retNpublicIds.addAll(uniqueIds);
    // // //     }
    // // //     return retNpublicIds;
    // // // }

    // // // private String createPublicId() {
    // // //     // int batchSize = publicIdGeneratorService.INITIAL_BATCH_SIZE; 
    // // //     for(int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
    // // //         // Genera un batch di PublicId
    // // //         List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID);

    // // //         // Verifica quali ID sono già presenti nel database
    // // //         List<String> existingIds = categoryRepository.findExistingPublicIds(publicIdBatch);

    // // //         // Filtra gli ID univoci
    // // //         List<String> uniqueIds = publicIdBatch.stream()
    // // //                                               .filter(id -> !existingIds.contains(id))
    // // //                                               .collect(Collectors.toList());

    // // //         // Se esiste almeno un ID univoco, lo restituisce
    // // //         if(!uniqueIds.isEmpty()) {
    // // //             return uniqueIds.get(0);
    // // //         }
    // // //     }
    // // //     throw new IllegalStateException("CategoryService - failed to generate unique publicId after " + MAX_ATTEMPTS + " batch attempts.");
    // // // }

    private String createPublicIdV2() {
        return publicIdGeneratorService.generateSinglePublicIdV2(PREFIX_PUBLIC_ID, categoryRepository::filterAvailablePublicIds);
    }

    private List<String> createBatchPublicIdsV2(int nPublicIds) {
        return publicIdGeneratorService.generateBatchPublicIdsV2(PREFIX_PUBLIC_ID, categoryRepository::filterAvailablePublicIds, nPublicIds);
    }

    private CategoryDTO mapToCategoryDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setPublicId(category.getPublicId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setImageUrl(category.getImageUrl());
        categoryDTO.setSlug(category.getSlug());
        categoryDTO.setIsActive(category.getIsActive());
        categoryDTO.setDisplayOrder(category.getDisplayOrder());
        categoryDTO.setNameParentCategory(category.getParentCategory().getName());
        return categoryDTO;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        // return categoryRepository.findAll();

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
            .map(this::mapToCategoryDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Category getCategoryByPublicId(String publicId) {
        return categoryRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Category with publicId " + publicId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByPublicIds(List<String> publicIds) {
        return categoryRepository.findByPublicIds(publicIds);
    }

    public CategoryDTO getCategoryDTOByPublicId(String publicId) {
        return mapToCategoryDTO(getCategoryByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new RuntimeException("Category with name " + name + " not found"));
    }

    public List<CategoryDTO> categoriesToCategoryDTOs(List<Category> categories) {
        return categories.stream()
            .map(this::mapToCategoryDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO createCategory(CategoryCreateUpdateDTO categoryCreateDTO) {
        Category category = getCategoryByName(categoryCreateDTO.getName());
        if(category != null) {
            throw new RuntimeException("Category with name " + categoryCreateDTO.getName() + " already exists.");
        }
        Category parentCategory = getCategoryByName(categoryCreateDTO.getNameParentCategory());
        if(parentCategory == null) {
            throw new RuntimeException("Parent Category with name " + categoryCreateDTO.getNameParentCategory() + " doesn't exists.");
        }

        category = new Category();
        category.setPublicId(createPublicIdV2());
        category.setName(categoryCreateDTO.getName());
        category.setDescription(categoryCreateDTO.getDescription());
        category.setImageUrl(categoryCreateDTO.getImageUrl());
        category.setSlug(categoryCreateDTO.getSlug());
        category.setIsActive(categoryCreateDTO.getIsActive());
        category.setDisplayOrder(categoryCreateDTO.getDisplayOrder());
        category.setParentCategory(parentCategory);
        category = categoryRepository.save(category);
        
        // update subcategories of category parent of category just created
        parentCategory.getSubCategories().add(category);
        categoryRepository.save(parentCategory);

        return mapToCategoryDTO(category);
    }

    @Transactional
    public CategoryDTO updateCategory(String publicId, CategoryCreateUpdateDTO categoryUpdateDTO) {
        Category parentCategory = getCategoryByName(categoryUpdateDTO.getNameParentCategory());
        if(parentCategory == null) {
            throw new RuntimeException("Parent Category with name " + categoryUpdateDTO.getNameParentCategory() + " doesn't exists.");
        }

        Category category = getCategoryByPublicId(publicId);
        category.setName(categoryUpdateDTO.getName());
        category.setDescription(categoryUpdateDTO.getDescription());
        category.setImageUrl(categoryUpdateDTO.getImageUrl());
        category.setSlug(categoryUpdateDTO.getSlug());
        category.setIsActive(categoryUpdateDTO.getIsActive());
        category.setDisplayOrder(categoryUpdateDTO.getDisplayOrder());
        category.setUpdatedAt(dateUtil.getCurrentDateTime());

        // if category doesn't have parent
        if(category.getParentCategory() == null) {
            category.setParentCategory(parentCategory);

            parentCategory.getSubCategories().add(category);
            categoryRepository.save(parentCategory);
            
            return mapToCategoryDTO(categoryRepository.save(category));
        }

        // old parent and new parent are the same
        if(category.getParentCategory().getName().equals(categoryUpdateDTO.getNameParentCategory())) {
            return mapToCategoryDTO(categoryRepository.save(category));
        }

        // new parent different from old 
        Category oldParentCategory = category.getParentCategory();
        List<Category> oldParentSubs = oldParentCategory.getSubCategories();
        for(int i=0; i<oldParentSubs.size(); i++) {
            if(oldParentSubs.get(i).getName().equals(category.getName())) {
                oldParentSubs.remove(i);
            }
        }
        oldParentCategory.setSubCategories(oldParentSubs);
        categoryRepository.save(oldParentCategory);

        category.setParentCategory(parentCategory);

        parentCategory.getSubCategories().add(category);
        categoryRepository.save(parentCategory);

        return mapToCategoryDTO(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(String publicId) {
        Category category = getCategoryByPublicId(publicId);
        
        if(category.getSubCategories()==null || category.getSubCategories().isEmpty()) {
            throw new RuntimeException("The category that you want to delete " + category.getName() + " has subcategories, they need a new parent before the delete of the category.");
        }

        Category parentCategory = category.getParentCategory();
        List<Category> parentSubs = parentCategory.getSubCategories();
        for(int i=0; i<parentSubs.size(); i++) {
            if(parentSubs.get(i).getName().equals(category.getName())) {
                parentSubs.remove(i);
            }
        }
        parentCategory.setSubCategories(parentSubs);
        categoryRepository.save(parentCategory);

        categoryRepository.deleteById(category.getId());
    }
}
