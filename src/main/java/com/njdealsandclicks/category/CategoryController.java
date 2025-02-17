package com.njdealsandclicks.category;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.category.CategoryCreateUpdateDTO;
import com.njdealsandclicks.dto.category.CategoryDTO;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{publicId}")
    public CategoryDTO getCategoryByPublicId(@PathVariable String publicId) {
        return categoryService.getCategoryDTOByPublicId(publicId);
    }
    
    @PostMapping("/create")
    public CategoryDTO createCategory(@RequestBody CategoryCreateUpdateDTO categoryCreateDTO) {
        return categoryService.createCategory(categoryCreateDTO);
    }

    @PutMapping("/{publicId}")
    public CategoryDTO updateCategory(@PathVariable String publicId, @RequestBody CategoryCreateUpdateDTO categoryUpdateDTO) {
        return categoryService.updateCategory(publicId, categoryUpdateDTO);
    }

    @DeleteMapping("/delete/{publicId}")
    public void deleteCategory(@PathVariable String publicId) {
        categoryService.deleteCategory(publicId);
    }
    
}
