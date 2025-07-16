package com.njdealsandclicks.article;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleCreateUpdateDTO;
import com.njdealsandclicks.dto.article.ArticleDTO;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticles();
    }

    @PostMapping("/create")
    public ArticleDTO createArticle(@RequestBody ArticleCreateUpdateDTO articleCreateDTO) {
        return articleService.createArticle(articleCreateDTO);
    }

    @PutMapping("/{publicId}")
    public ArticleDTO updateArticle(@PathVariable String publicId, @RequestBody ArticleCreateUpdateDTO articleUpdateDTO) {
        return articleService.updateArticle(publicId, articleUpdateDTO);
    }

    @DeleteMapping("/deletesoft/{publicId}")
    public void softDeleteCategory(@PathVariable String publicId) {
        articleService.softDeleteArticle(publicId);
    }

    // @DeleteMapping("/delete/{publicId}")
    // public void deleteCategory(@PathVariable String publicId) {
    //     articleService.deleteArticle(publicId);
    // }
}
