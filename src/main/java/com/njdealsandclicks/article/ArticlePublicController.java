package com.njdealsandclicks.article;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.product.ProductDTO;

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

    @GetMapping("/{slug}")
    public ArticleDTO getArticleBySlug(@PathVariable("slug") String slug) {
        return articleService.getArticleDTOBySlug(slug);
    }

    @GetMapping("/{slug}/related-products")
    public List<ProductDTO> getRelatedProductsByArticle(@PathVariable("slug") String slug) {
        return articleService.getRelatedProductsByArticleSlug(slug, 6); // esempio: max 6 prodotti
    }

    @GetMapping("/{slug}/related-articles")
    public List<ArticleDTO> getRelatedArticlesByArticle(@PathVariable("slug") String slug) {
        return articleService.getRelatedArticlesByArticleSlug(slug, 3); // esempio: max 3 articoli
    }

}
