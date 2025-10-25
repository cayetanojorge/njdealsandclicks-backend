package com.njdealsandclicks.productmarket;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.product.ProductDetailPricesDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;
import com.njdealsandclicks.recommendation.RecommendationService;


@RestController
@RequestMapping("/api/market/{country}/product")
public class ProductMarketController {
    
    private final ProductMarketService productMarketService;
    private final RecommendationService recommendationService;

    public ProductMarketController(ProductMarketService productMarketService, RecommendationService recommendationService) {
        this.productMarketService = productMarketService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/")
    public List<ProductDetailsDTO> getAllProductMarkets(@PathVariable String country) {
        return productMarketService.getAllProductMarkets(country);
    }

    @GetMapping("/{publicId}")
    public ProductDetailPricesDTO getProductMarketByPublicId(@PathVariable String country, 
                                                @PathVariable String publicId) {
        return productMarketService.getProductMarketDetailsDTOsByPublicId(country, publicId);
    }

    // @GetMapping("/category/{categoryPublicId}")
    // public List<ProductDTO> getProductsByCategoryId(@PathVariable String categoryPublicId) {
    //     return productService.getProductDTOsByCategoryId(categoryPublicId);
    // }

    // // @GetMapping("/{id}/price-history")
    // // public List<PriceHistory> getPriceHistoryProductById(@PathVariable Long id) {
    // //     return productService.getPriceHistoryByProductId(id);
    // // }

    // @PostMapping("/create")
    // public ProductDTO createProduct(@RequestBody ProductCreateUpdateDTO productCreateDTO) {
    //     return productService.createProduct(productCreateDTO);
    // }
    
    // @PutMapping("/{publicId}")
    // public ProductDTO updateProduct(@PathVariable("publicId") String publicId, @RequestBody ProductCreateUpdateDTO productUpdateDTO) {
    //     return productService.updateProduct(publicId, productUpdateDTO);
    // }

    // @DeleteMapping("/delete/{publicId}")
    // public void deleteProduct(@PathVariable("publicId") String publicId) {
    //     productService.deleteProduct(publicId);
    // }

    // ----------- per pagina home search -----------
    @GetMapping("/search")
    public List<ProductDetailsDTO> searchProductMarkets(
                                @PathVariable String country,
                                @RequestParam("q") String q,
                                @RequestParam(name="limit", defaultValue = "12") int limit) {
        return productMarketService.searchProducts(q, limit, country);
    }

    // ----------- per pagina product details -----------
    // prodotti correlati in base al prodotto, la cui pagina e' stata aperta
    @GetMapping("/{publicId}/related-products")
    public List<ProductDetailsDTO> getRelatedProductsByProductPublicIdAndCountry(
                                        @PathVariable String country, 
                                        @PathVariable("publicId") String publicId,
                                        @RequestParam(name="maxResults", defaultValue = "6") int maxResults) {
        return recommendationService.getRelatedProductsByProductMarketPublicIdAndCountry(publicId, maxResults, country); // es: massimo 6 correlati
    }
}
