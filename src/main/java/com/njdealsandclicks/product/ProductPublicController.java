package com.njdealsandclicks.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;


@RestController
@RequestMapping("/api/public/products")
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
    public ProductDetailsDTO getProductByPublicId(@PathVariable String publicId) {
        return productService.getProductDetailsDTOsByPublicId(publicId);
    }

    @GetMapping("/category/{categoryPublicId}")
    public List<ProductDTO> getProductsByCategoryId(@PathVariable String categoryPublicId) {
        return productService.getProductDTOsByCategoryId(categoryPublicId);
    }
}
