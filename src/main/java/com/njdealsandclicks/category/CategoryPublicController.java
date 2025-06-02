package com.njdealsandclicks.category;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.category.CategoryDTO;



@RestController
@RequestMapping("/api/public/category")
public class CategoryPublicController {
    
    private final CategoryService categoryService;

    public CategoryPublicController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{publicId}")
    public CategoryDTO getCategoryByPublicId(@PathVariable("publicId") String publicId) {
        return categoryService.getCategoryDTOByPublicId(publicId);
    }
    
}
