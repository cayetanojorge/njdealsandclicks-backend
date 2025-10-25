package com.njdealsandclicks.recommendation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.njdealsandclicks.article.Article;
import com.njdealsandclicks.article.ArticleService;
import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;
import com.njdealsandclicks.productmarket.ProductMarket;
import com.njdealsandclicks.productmarket.ProductMarketService;


@Service
public class RecommendationService {
    
    private final ArticleService articleService;
    private final ProductMarketService productMarketService;

    public RecommendationService(ArticleService articleService, ProductMarketService productMarketService) {
        this.articleService = articleService;
        this.productMarketService = productMarketService;
    }

    // ----------- per pagina article details -----------
    // prodotti correlati in base ai prodotti menzionati dall'articolo
    public List<ProductDetailsDTO> getRelatedProductsByArticleSlugAndCountry(String slug, int maxResults, String countryCode) {
        Article article = articleService.getArticleBySlug(slug);
        return productMarketService.findRelatedProductsByArticleAndCountry(article, maxResults, countryCode);
    }

    // articoli correlati in base all'articolo
    public List<ArticleDTO> getRelatedArticlesByArticleSlug(String slug, int maxResults) {
        Article article = articleService.getArticleWithProductsBySlug(slug);
        return articleService.findRelatedArticlesByArticle(article, maxResults);
    }


    // ----------- per pagina product details -----------
    // articoli che menzionano il prodotto
    public List<ArticleDTO> getArticlesThatMentionProduct(String productPublicId) {
        return articleService.findArticlesThatMentionProduct(productPublicId);
    }

    // prodotti correlati in base al prodotto, la cui pagina e' stata aperta
    public List<ProductDetailsDTO> getRelatedProductsByProductMarketPublicIdAndCountry(String productMarketPublicId, int maxResults, String countryCode) {
        ProductMarket productMarket = productMarketService.getProductMarketByPublicId(productMarketPublicId);
        return productMarketService.findRelatedProductsByProductAndCountry(productMarket, maxResults, countryCode);
    }

}
