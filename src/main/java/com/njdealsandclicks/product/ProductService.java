package com.njdealsandclicks.product;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.category.CategoryService;
import com.njdealsandclicks.currency.Currency;
import com.njdealsandclicks.currency.CurrencyService;
import com.njdealsandclicks.dto.category.CategoryCreateUpdateDTO;
import com.njdealsandclicks.dto.product.ProductCreateUpdateDTO;
import com.njdealsandclicks.dto.product.ProductDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;
import com.njdealsandclicks.pricehistory.PriceHistory;
import com.njdealsandclicks.pricehistory.PriceHistoryService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

/**
 * Il servizio contiene la logica per gestire i prodotti e usa il repository.
 */

@Service
public class ProductService {

    private static final int MAX_ATTEMPTS = 3; // n massimo di tentativi di batch per generare publicId
    private static final String PREFIX_PUBLIC_ID = "prod_";

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;
    private final CategoryService categoryService;
    private final CurrencyService currencyService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;


    public ProductService(ProductRepository productRepository, PriceHistoryService priceHistoryService, CategoryService categoryService,
                            CurrencyService currencyService, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.productRepository = productRepository;
        this.priceHistoryService = priceHistoryService;
        this.categoryService = categoryService;
        this.currencyService = currencyService;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        // int batchSize = publicIdGeneratorService.INITIAL_BATCH_SIZE; 
        for(int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Genera un batch di PublicId
            List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID);

            // Verifica quali ID sono già presenti nel database
            List<String> existingIds = productRepository.findExistingPublicIds(publicIdBatch);

            // Filtra gli ID univoci
            List<String> uniqueIds = publicIdBatch.stream()
                                                  .filter(id -> !existingIds.contains(id))
                                                  .collect(Collectors.toList());

            // Se esiste almeno un ID univoco, lo restituisce
            if(!uniqueIds.isEmpty()) {
                return uniqueIds.get(0);
            }

            // Aumenta dinamicamente il batch size per il prossimo tentativo
            // batchSize = adjustBatchSize(batchSize, existingIds.size()); // e passarlo al generatePublicIdBatch()
        }

        throw new IllegalStateException("ProductService - failed to generate unique publicId after " + MAX_ATTEMPTS + " batch attempts.");
    }

    /*
    private int adjustBatchSize(int currentSize, int collisionCount) {
        if(collisionCount > 0) {
            return Math.min(currentSize * 2, 100);
        }
        return currentSize;
    */

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPublicId(product.getPublicId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCurrentPrice(product.getCurrentPrice());
        productDTO.setAffiliateLink(product.getAffiliateLink());
        productDTO.setCategoryName(product.getCategory().getName());
        return productDTO;
    }

    private ProductDetailsDTO mapToProductDetailsDTO(Product product) {
        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setPublicId(product.getPublicId());
        productDetailsDTO.setName(product.getName());
        productDetailsDTO.setDescription(product.getDescription());
        productDetailsDTO.setCurrentPrice(product.getCurrentPrice());
        productDetailsDTO.setAffiliateLink(product.getAffiliateLink());
        productDetailsDTO.setCategoryName(product.getCategory().getName());
        productDetailsDTO.setPriceHistoryDTOs(priceHistoryService.getPriceHistoriesDTOsByProductPublicId(product.getPublicId()));
        return productDetailsDTO;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        // return productRepository.findAll();
        
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Product getProductByPublicId(String publicId) {
        return productRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Product with publicId " + publicId + " not found"));
    }

    public ProductDetailsDTO getProductDetailsDTOsByPublicId(String publicId) {
        return mapToProductDetailsDTO(getProductByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByPublicIds(List<String> publicIds) {
        return productRepository.findByPublicIds(publicIds);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductDTOsByCategoryId(String categoryPublicId) {
        List<Product> products = productRepository.findByCategoryPublicId(categoryPublicId);
        return products.stream()
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }

    public List<ProductDTO> productsToProductDTOs(List<Product> products) {
        return products.stream()
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }

    // @Transactional(readOnly = true)
    // public List<PriceHistory> getPriceHistoryByProductId(Long id) {
    //     return getProductById(id).getPriceHistories();
    // }

    @Transactional
    public ProductDTO createProduct(ProductCreateUpdateDTO productCreateDTO) {

        if(productRepository.existsByAffiliateLink(productCreateDTO.getAffiliateLink())) {
            throw new RuntimeException("Product with affiliate link " + productCreateDTO.getAffiliateLink() + " already exists.");
        }
        
        Currency currency = currencyService.getCurrencyByCode(productCreateDTO.getCurrencyCode());
        Category category = categoryService.getCategoryByName(productCreateDTO.getCategoryName());

        Product product = new Product();
        product.setPublicId(createPublicId());
        product.setName(productCreateDTO.getName());
        product.setDescription(productCreateDTO.getDescription());
        product.setCurrency(currency);
        product.setCurrentPrice(productCreateDTO.getCurrentPrice());
        product.setAffiliateLink(productCreateDTO.getAffiliateLink());
        product.setRating(productCreateDTO.getRating());
        product.setReviewCount(productCreateDTO.getReviewCount());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPrice(savedProduct.getCurrentPrice());
        priceHistory.setRecordedAt(ZonedDateTime.now(ZoneOffset.UTC));
        priceHistory.setProduct(savedProduct);
        priceHistoryService.createPriceHistory(priceHistory);

        return mapToProductDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(String publicId, ProductCreateUpdateDTO productUpdateDTO) {
        Product product = getProductByPublicId(publicId);

        // handle category
        Category category = categoryService.getCategoryByName(productUpdateDTO.getName());
        if(category == null) {
            CategoryCreateUpdateDTO categoryCreateDTO = new CategoryCreateUpdateDTO();
            categoryCreateDTO.setName(productUpdateDTO.getCategoryName());
            category = categoryService.getCategoryByPublicId(categoryService.createCategory(categoryCreateDTO).getPublicId());
        }

        // create record price history if price changed
        if(!product.getCurrentPrice().equals(productUpdateDTO.getCurrentPrice())) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setPrice(productUpdateDTO.getCurrentPrice());
            priceHistory.setRecordedAt(ZonedDateTime.now(ZoneOffset.UTC));
            priceHistory.setProduct(product);
            priceHistoryService.createPriceHistory(priceHistory);
        }

        // update product
        product.setName(productUpdateDTO.getName());
        product.setDescription(productUpdateDTO.getDescription());
        product.setCurrentPrice(productUpdateDTO.getCurrentPrice());
        product.setAffiliateLink(productUpdateDTO.getAffiliateLink());
        product.setCategory(category);
        product.setUpdatedAt(dateUtil.getCurrentDateTime());
        
        return mapToProductDTO(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(String publicId) {
        Product product = getProductByPublicId(publicId);
        // productRepository.delete(getProductById(id));
        productRepository.deleteById(product.getId());
    }
}
