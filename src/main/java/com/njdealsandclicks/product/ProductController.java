package com.njdealsandclicks.product;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.product.ProductCreateUpdateDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;

/**
 * Il controller definisce gli endpoint per gestire i prodotti.
 */

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;

    public ProductController(ProductService productService) {
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

    // @GetMapping("/{id}/price-history")
    // public List<PriceHistory> getPriceHistoryProductById(@PathVariable Long id) {
    //     return productService.getPriceHistoryByProductId(id);
    // }

    @PostMapping("/create")
    public ProductDTO createProduct(@RequestBody ProductCreateUpdateDTO productCreateDTO) {
        return productService.createProduct(productCreateDTO);
    }
    
    @PutMapping("/{publicId}")
    public ProductDTO updateProduct(@PathVariable("publicId") String publicId, @RequestBody ProductCreateUpdateDTO productUpdateDTO) {
        return productService.updateProduct(publicId, productUpdateDTO);
    }

    @DeleteMapping("/delete/{publicId}")
    public void deleteProduct(@PathVariable("publicId") String publicId) {
        productService.deleteProduct(publicId);
    }
}
