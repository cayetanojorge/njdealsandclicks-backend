package com.njdealsandclicks.article;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.recommendation.RecommendationService;

@RestController
@RequestMapping("/api/public/article")
public class ArticlePublicController {
    
    private final ArticleService articleService;
    private final RecommendationService recommendationService;


    public ArticlePublicController(ArticleService articleService, RecommendationService recommendationService) {
        this.articleService = articleService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/")
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticlesIsDeletedFalseAndIsPublishedTrue();
    }

    @GetMapping("/{slug}")
    public ArticleDTO getArticleBySlug(@PathVariable("slug") String slug, @RequestParam("market") String market) {
        return articleService.getArticleDTOBySlugAndCountry(slug, market);
    }

    // ----------- per pagina article details -----------
    @GetMapping("/{slug}/related-products") // ancora NON usato nel frontend
    public List<ProductDTO> getRelatedProductsByArticle(@PathVariable("slug") String slug, @RequestParam("market") String market) {
        return recommendationService.getRelatedProductsByArticleSlugAndCountry(slug, 6, market); // esempio: max 6 prodotti
    }

    @GetMapping("/{slug}/related-articles")
    public List<ArticleDTO> getRelatedArticlesByArticle(@PathVariable("slug") String slug) {
        return recommendationService.getRelatedArticlesByArticleSlug(slug, 3); // esempio: max 3 articoli
    }
    // ----------- ----------- ----------- ----------- -----------

}
