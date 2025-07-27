package com.njdealsandclicks.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;


@RestController
@RequestMapping("/api/public/product")
public class ProductPublicController {
    
    private final ProductService productService;

    public ProductPublicController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{publicId}")
    public ProductDetailsDTO getProductByPublicId(@PathVariable("publicId") String publicId) {
        return productService.getProductDetailsDTOsByPublicId(publicId);
    }

    @GetMapping("/category/{categoryPublicId}")
    public List<ProductDTO> getProductsByCategoryId(@PathVariable("categoryPublicId") String categoryPublicId) {
        return productService.getProductDTOsByCategoryId(categoryPublicId);
    }

    @GetMapping("/{publicId}/mentioned-in-articles")
    public List<ArticleDTO> getArticlesThatMentionProduct(@PathVariable("publicId") String publicId) {
        return productService.getArticlesThatMentionProduct(publicId);
    }

    @GetMapping("/{publicId}/related-products")
    public List<ProductDTO> getRelatedProducts(@PathVariable("publicId") String publicId) {
        return productService.getRelatedProducts(publicId, 6); // es: massimo 6 correlati
    }
}
