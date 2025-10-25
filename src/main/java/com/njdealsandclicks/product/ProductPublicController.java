package com.njdealsandclicks.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.recommendation.RecommendationService;


@RestController
@RequestMapping("/api/public/product")
public class ProductPublicController {
    
    // private final ProductService productService;
    private final RecommendationService recommendationService;

    public ProductPublicController(//ProductService productService, 
                        RecommendationService recommendationService) {
        // this.productService = productService;
        this.recommendationService = recommendationService;
    }

    // @GetMapping("/all")
    // public List<ProductDTO> getAllProducts() {
    //     return productService.getAllProducts();
    // }

    // @GetMapping("/")
    // public List<ProductDTO> getAllProductsByMarket(@RequestParam("market") String market) {
    //     return productService.getAllProductsByMarket(market);
    // }

    // @GetMapping("/{publicId}")
    // public ProductDetailsDTO getProductByPublicId(@PathVariable("publicId") String publicId) {
    //     return productService.getProductDetailsDTOsByPublicId(publicId);
    // }

    // @GetMapping("/category/{categoryPublicId}")
    // public List<ProductDTO> getProductsByCategoryId(@PathVariable("categoryPublicId") String categoryPublicId) {
    //     return productService.getProductDTOsByCategoryId(categoryPublicId);
    // }

    // ----------- per pagina product details -----------
    @GetMapping("/{publicId}/mentioned-in-articles")
    public List<ArticleDTO> getArticlesThatMentionProduct(@PathVariable("publicId") String publicId) {
        return recommendationService.getArticlesThatMentionProduct(publicId);
    }

}
