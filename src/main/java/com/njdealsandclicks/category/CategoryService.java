package com.njdealsandclicks.category;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.category.CategoryCreateUpdateDTO;
import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class CategoryService {

    private static final String PREFIX_PUBLIC_ID = "categ_";

    private final CategoryRepository categoryRepository;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;
    

    public CategoryService(CategoryRepository categoryRepository, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.categoryRepository = categoryRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicIdV2(PREFIX_PUBLIC_ID, categoryRepository::filterAvailablePublicIds);
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
        if (Objects.nonNull(category.getParentCategory())) {
            categoryDTO.setNameParentCategory(category.getParentCategory().getName());
        }
        return categoryDTO;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
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
        category.setPublicId(createPublicId());
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
