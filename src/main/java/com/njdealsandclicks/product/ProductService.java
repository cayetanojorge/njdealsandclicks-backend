package com.njdealsandclicks.product;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.njdealsandclicks.article.Article;
import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.category.CategoryService;
import com.njdealsandclicks.country.Country;
import com.njdealsandclicks.country.CountryService;
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

    private static final String PREFIX_PUBLIC_ID = "prod_";

    private final ProductRepository productRepository;
    private final PriceHistoryService priceHistoryService;
    private final CategoryService categoryService;
    // private final CurrencyService currencyService;
    private final CountryService countryService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;


    public ProductService(ProductRepository productRepository, PriceHistoryService priceHistoryService, CategoryService categoryService,
                            /*CurrencyService currencyService*/ CountryService countryService,
                            PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.productRepository = productRepository;
        this.priceHistoryService = priceHistoryService;
        this.categoryService = categoryService;
        // this.currencyService = currencyService;
        this.countryService = countryService;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, productRepository::filterAvailablePublicIds);
    }

    private ProductDTO mapToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setPublicId(product.getPublicId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCountryDTO(countryService.getCountryDTOByPublicId(product.getCountry().getPublicId()));
        productDTO.setCurrentPrice(product.getCurrentPrice());
        productDTO.setAffiliateLink(product.getAffiliateLink());
        productDTO.setRating(product.getRating());
        productDTO.setReviewCount(product.getReviewCount());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setBrand(product.getBrand());
        productDTO.setTags(product.getTags());
        productDTO.setFeatures(product.getFeatures());
        productDTO.setCategoryName(product.getCategory().getName());
        return productDTO;
    }

    private ProductDetailsDTO mapToProductDetailsDTO(Product product) {
        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();
        productDetailsDTO.setPublicId(product.getPublicId());
        productDetailsDTO.setName(product.getName());
        productDetailsDTO.setDescription(product.getDescription());
        productDetailsDTO.setCountryDTO(countryService.getCountryDTOByPublicId(product.getCountry().getPublicId()));
        productDetailsDTO.setCurrentPrice(product.getCurrentPrice());
        productDetailsDTO.setAffiliateLink(product.getAffiliateLink());
        productDetailsDTO.setRating(product.getRating());
        productDetailsDTO.setReviewCount(product.getReviewCount());
        productDetailsDTO.setImageUrl(product.getImageUrl());
        productDetailsDTO.setBrand(product.getBrand());
        productDetailsDTO.setTags(product.getTags());
        productDetailsDTO.setFeatures(product.getFeatures());
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
    public List<ProductDTO> getAllProductsByMarket(String countryCode) {
        return productRepository.findByCountryCode(countryCode).stream()
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
        
        Country country = countryService.getCountryByCode(productCreateDTO.getCountryCode());
        Category category = categoryService.getCategoryByName(productCreateDTO.getCategoryName());

        Product product = new Product();
        product.setPublicId(createPublicId());
        product.setName(productCreateDTO.getName());
        product.setDescription(productCreateDTO.getDescription());
        product.setCountry(country);
        product.setCurrentPrice(productCreateDTO.getCurrentPrice());
        product.setAffiliateLink(productCreateDTO.getAffiliateLink());
        product.setRating(productCreateDTO.getRating());
        product.setReviewCount(productCreateDTO.getReviewCount());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setPrice(savedProduct.getCurrentPrice());
        priceHistory.setRecordedAt(dateUtil.getCurrentDateTime());
        priceHistory.setProduct(savedProduct);
        priceHistoryService.createPriceHistory(priceHistory);

        return mapToProductDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(String publicId, ProductCreateUpdateDTO productUpdateDTO) {
        Product product = getProductByPublicId(publicId);

        Double oldPrice = product.getCurrentPrice();
        Country country = countryService.getCountryByCode(productUpdateDTO.getCountryCode());
        Category category = categoryService.getCategoryByName(productUpdateDTO.getCategoryName());

        // update product
        product.setName(productUpdateDTO.getName());
        product.setDescription(productUpdateDTO.getDescription());
        product.setCountry(country);
        product.setCurrentPrice(productUpdateDTO.getCurrentPrice());
        product.setAffiliateLink(productUpdateDTO.getAffiliateLink());
        product.setRating(productUpdateDTO.getRating());
        product.setReviewCount(productUpdateDTO.getReviewCount());
        product.setCategory(category);
        ZonedDateTime currentDateTime = dateUtil.getCurrentDateTime();
        product.setUpdatedAt(currentDateTime);
        
        Product savedProduct = productRepository.save(product);

        // create record price history if price changed
        if(!oldPrice.equals(productUpdateDTO.getCurrentPrice())) {
            PriceHistory priceHistory = new PriceHistory();
            priceHistory.setPrice(productUpdateDTO.getCurrentPrice());
            priceHistory.setRecordedAt(currentDateTime);
            priceHistory.setProduct(savedProduct);
            priceHistoryService.createPriceHistory(priceHistory);
        }

        return mapToProductDTO(savedProduct);
    }

    @Transactional
    public void deleteProduct(String publicId) {
        Product product = getProductByPublicId(publicId);

        // prima elimino referenze al record eliminando i record da price history
        product.getPriceHistories().clear(); // oppure priceHistoryRepository.deleteByProduct(product);
        
        // poi posso procedere ad eliminare a eliminare il prodotto
        // productRepository.delete(getProductById(id));
        productRepository.deleteById(product.getId());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findRelatedProductsByArticleAndCountry(Article article, int maxResults, String countryCode) {
        
        // raccogli publicId dei prodotti menzionati
        List<String> articleProdPublicIds = new ArrayList<>();
        for (Product product : article.getProducts()) {
            articleProdPublicIds.add(product.getPublicId());
        }
        // prodotti completi dal DB (per avere tag e categoria)
        List<Product> originalProducts = productRepository.findByPublicIdsAndCountry(articleProdPublicIds, countryCode);

        // tag combinati
        Set<String> relatedTags = originalProducts.stream()
            .map(Product::getTags)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .collect(Collectors.toSet());

        // prendi una categoria valida tra i prodotti (la prima trovata non-null)
        // futuro: considera piu' cagetorie, dato i prodotti in lista
        String mainCategory = originalProducts.stream()
            .map(p -> p.getCategory() != null ? p.getCategory().getName() : null)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);

        return productRepository.findRelatedProductsByCountry(
                articleProdPublicIds,
                new ArrayList<>(relatedTags),
                !relatedTags.isEmpty(),
                mainCategory,
                countryCode,
                maxResults
            ).stream()
            // .filter(p -> p.getCountry().getCode().equalsIgnoreCase(countryCode)) // qui puoi anche filtrare di nuovo per market, se la native non lo fa
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findRelatedProductsByProductAndCountry(Product product, int maxResults, String countryCode) {
        // Tag del prodotto
        List<String> tags = product.getTags() != null && !product.getTags().isEmpty()
            ? product.getTags() 
            : List.of();

        // Categoria del prodotto
        String categoryName = product.getCategory() != null
            ? product.getCategory().getName()
            : null;

        // Query per trovare prodotti correlati (escludendo quello attuale)
        return productRepository.findRelatedProductsByCountry(
                List.of(product.getPublicId()), // excludeIds
                tags,
                !tags.isEmpty(),
                categoryName,
                countryCode,
                maxResults
            ).stream()
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String q, int limit, String countryCode) {
        if (!StringUtils.hasText(q)) return List.of();

        // Normalizza query (trim ecc.)
        String query = q.trim();

        // Semplice ricerca testuale su name/brand/description + tags
        List<Product> products = productRepository.searchByTextAndCountry(query, limit, countryCode);

        return products.stream()
            .map(this::mapToProductDTO)
            .collect(Collectors.toList());
    }
}
