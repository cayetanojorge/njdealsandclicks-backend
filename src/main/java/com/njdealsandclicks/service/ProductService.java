package com.njdealsandclicks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.njdealsandclicks.model.Product;
import com.njdealsandclicks.repository.ProductRepository;


/**
 * Il servizio contiene la logica per gestire i prodotti e usa il repository.
 */


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product geProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product with id not found"));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = geProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        // productRepository.delete(geProductById(id));
        productRepository.deleteById(id);
    }
}
