package com.njdealsandclicks.product;

// import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.pricehistory.PriceHistory;

// import com.njdealsandclicks.pricehistory.PriceHistory;


/**
 * Il servizio contiene la logica per gestire i prodotti e usa il repository.
 */


@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(Long id) {
        return productRepository.findByCategoryId(id);
    }

    @Transactional(readOnly = true)
    public List<PriceHistory> getPriceHistoryByProductId(Long id) {
        return getProductById(id).getPriceHistories();
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setCurrentPrice(productDetails.getCurrentPrice());
        product.setAffiliateLink(productDetails.getAffiliateLink());
        product.setCategory(productDetails.getCategory());
        /* per priceHistory */
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        // productRepository.delete(getProductById(id));
        productRepository.deleteById(id);
    }
}
