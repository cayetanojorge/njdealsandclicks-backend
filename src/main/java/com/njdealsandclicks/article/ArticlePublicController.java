package com.njdealsandclicks.article;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleDTO;

@RestController
@RequestMapping("/api/public/article")
public class ArticlePublicController {
    
    private final ArticleService articleService;

    public ArticlePublicController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticlesIsDeletedFalseAndIsPublishedTrue();
    }
}
