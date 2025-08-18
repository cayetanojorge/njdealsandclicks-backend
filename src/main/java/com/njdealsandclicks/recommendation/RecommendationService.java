package com.njdealsandclicks.recommendation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.njdealsandclicks.article.Article;
import com.njdealsandclicks.article.ArticleService;
import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.product.ProductService;


@Service
public class RecommendationService {
    
    private final ArticleService articleService;
    private final ProductService productService;

    public RecommendationService(ArticleService articleService, ProductService productService) {
        this.articleService = articleService;
        this.productService = productService;
    }

    // ----------- per pagina article details -----------
    public List<ProductDTO> getRelatedProductsByArticleSlugAndCountry(String slug, int maxResults, String countryCode) {
        Article article = articleService.getArticleBySlug(slug);
        return productService.findRelatedProductsByArticleAndCountry(article, maxResults, countryCode);
    }

    public List<ArticleDTO> getRelatedArticlesByArticleSlug(String slug, int maxResults) {
        Article article = articleService.getArticleWithProductsBySlug(slug);
        return articleService.findRelatedArticlesByArticle(article, maxResults);
    }


    // ----------- per pagina product details -----------
    public List<ArticleDTO> getArticlesThatMentionProduct(String productPublicId) {
        return articleService.findArticlesThatMentionProduct(productPublicId);
    }

    public List<ProductDTO> getRelatedProductsByProductPublicIdAndCountry(String productPublicId, int maxResults, String countryCode) {
        Product product = productService.getProductByPublicId(productPublicId);
        return productService.findRelatedProductsByProductAndCountry(product, maxResults, countryCode);
    }

}
