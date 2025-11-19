package com.njdealsandclicks.productmarket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.njdealsandclicks.article.Article;
import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.dto.product.ProductDetailPricesDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;
import com.njdealsandclicks.dto.productmarket.ProductMarketDTO;
import com.njdealsandclicks.dto.productmarket.ProductMarketDetailsDTO;
import com.njdealsandclicks.pricehistory.PriceHistoryService;
import com.njdealsandclicks.product.Product;


@Service
public class ProductMarketService {

    // private static final String PREFIX_PUBLIC_ID = "prod_mkt_";

    private final ProductMarketRepository productMarketRepository;
    private final PriceHistoryService priceHistoryService;
    // private final CountryService countryService;

    // private final PublicIdGeneratorService publicIdGeneratorService;
    // private final DateUtil dateUtil;


    public ProductMarketService(ProductMarketRepository productMarketRepository, PriceHistoryService priceHistoryService//, 
                            // CountryService countryService, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil
                            ) {
        this.productMarketRepository = productMarketRepository;
        this.priceHistoryService = priceHistoryService;
        // this.countryService = countryService;
        // this.publicIdGeneratorService = publicIdGeneratorService;
        // this.dateUtil = dateUtil;
    }

    // private String createPublicId() {
    //     return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, productMarketRepository::filterAvailablePublicIds);
    // }

    private ProductDetailsDTO mapToProductDetailsDTO(ProductMarket productMarket) {
        Product product = productMarket.getProduct();

        ProductDetailsDTO productDetailsDTO = new ProductDetailsDTO();
        // product
        productDetailsDTO.setPublicId(product.getPublicId());
        productDetailsDTO.setName(product.getName());
        productDetailsDTO.setDescription(product.getDescription());
        productDetailsDTO.setImageUrl(product.getImageUrl());
        productDetailsDTO.setBrand(product.getBrand());
        productDetailsDTO.setTags(product.getTags());
        productDetailsDTO.setFeatures(product.getFeatures());
        productDetailsDTO.setCategoryName(product.getCategory().getName());
        // product market
        productDetailsDTO.setProductMarketDTO(mapToProductMarketDTO(productMarket));
        
        return productDetailsDTO;
    }

    private ProductMarketDTO mapToProductMarketDTO(ProductMarket productMarket) {
        ProductMarketDTO productMarketDTO = new ProductMarketDTO();
        productMarketDTO.setCountry(productMarket.getCountry());
        productMarketDTO.setAffiliateLink(productMarket.getAffiliateLink());
        productMarketDTO.setCurrentPrice(productMarket.getCurrentPrice());
        productMarketDTO.setRating(productMarket.getRating());
        productMarketDTO.setReviewCount(productMarket.getReviewCount());
        productMarketDTO.setIsAvailable(productMarket.getIsAvailable());
        productMarketDTO.setImageUrl(productMarket.getImageUrl());
        // TODO fare map availabilityByCountry
        return productMarketDTO;
    }

    private ProductDetailPricesDTO mapToProductDetailPricesDTO(ProductMarket productMarket) {
        Product product = productMarket.getProduct();

        ProductDetailPricesDTO productDetailsDTO = new ProductDetailPricesDTO();
        // product
        productDetailsDTO.setPublicId(product.getPublicId());
        productDetailsDTO.setName(product.getName());
        productDetailsDTO.setDescription(product.getDescription());
        productDetailsDTO.setImageUrl(product.getImageUrl());
        productDetailsDTO.setBrand(product.getBrand());
        productDetailsDTO.setTags(product.getTags());
        productDetailsDTO.setFeatures(product.getFeatures());
        productDetailsDTO.setCategoryName(product.getCategory().getName());
        // product market
        productDetailsDTO.setProductMarketDetailsDTO(mapToProductMarketDetailsDTO(productMarket));
        
        return productDetailsDTO;
    }

    private ProductMarketDetailsDTO mapToProductMarketDetailsDTO(ProductMarket productMarket) {
        ProductMarketDetailsDTO productMarketDetailsDTO = new ProductMarketDetailsDTO();
        productMarketDetailsDTO.setCountry(productMarket.getCountry());
        productMarketDetailsDTO.setExternalSku(productMarket.getExternalSku());
        productMarketDetailsDTO.setAffiliateLink(productMarket.getAffiliateLink());
        productMarketDetailsDTO.setCurrentPrice(productMarket.getCurrentPrice());
        productMarketDetailsDTO.setRating(productMarket.getRating());
        productMarketDetailsDTO.setReviewCount(productMarket.getReviewCount());
        productMarketDetailsDTO.setIsAvailable(productMarket.getIsAvailable());
        productMarketDetailsDTO.setImageUrl(productMarket.getImageUrl());
        productMarketDetailsDTO.setLastCheckedAt(productMarket.getLastCheckedAt());
        productMarketDetailsDTO.setPriceHistoryDTOs(priceHistoryService.getPriceHistoriesDTOsByProductMarketPublicId(productMarket.getPublicId()));
        // TODO fare map availabilityByCountry
        return productMarketDetailsDTO;
    }

    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> getAllProductMarkets(String countryCode) {        
        List<ProductMarket> productMarkets = productMarketRepository.findAllByCountry_CodeOrderByCurrentPriceAsc(countryCode);
        return productMarkets.stream()
            .map(this::mapToProductDetailsDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductMarket getProductMarketById(@NonNull UUID id) {
        return productMarketRepository.findById(id).orElseThrow(() -> new RuntimeException("ProductMarket with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public ProductMarket getProductMarketByPublicId(String publicId) {
        return productMarketRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("ProductMarket with publicId " + publicId + " not found"));
    }

    @Transactional(readOnly = true)
    public Optional<ProductMarket> findByProductAndCountry(Product product, String countryCode) {
        return productMarketRepository.findByProduct_PublicIdAndCountry_Code(product.getPublicId(), countryCode);
    }

    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> findByProductAndCountry(List<String> productPublicIds, String countryCode) {
        List<ProductMarket> productMarkets = productMarketRepository.findAllByProductPublicIdsAndCountry(productPublicIds, countryCode);
        return productMarkets.stream()
            .map(this::mapToProductDetailsDTO)
            .collect(Collectors.toList());

    }

    public ProductDetailPricesDTO getProductMarketDetailsDTOsByPublicId(String countryCode, String publicId) {
        return mapToProductDetailPricesDTO(getProductMarketByPublicId(publicId));
    }

    // @Transactional(readOnly = true)
    // public List<ProductMarket> getProductMarketsByPublicIds(List<String> publicIds) {
    //     return productMarketRepository.findByPublicIds(publicIds);
    // }

    // @Transactional(readOnly = true)
    // public List<ProductDTO> getProductDTOsByCategoryId(String categoryPublicId) {
    //     List<Product> products = productRepository.findByCategoryPublicId(categoryPublicId);
    //     return products.stream()
    //         .map(this::mapToProductDTO)
    //         .collect(Collectors.toList());
    // }

    // public List<ProductDTO> productsToProductDTOs(List<Product> products) {
    //     return products.stream()
    //         .map(this::mapToProductDTO)
    //         .collect(Collectors.toList());
    // }

    // @Transactional(readOnly = true)
    // public List<PriceHistory> getPriceHistoryByProductId(Long id) {
    //     return getProductById(id).getPriceHistories();
    // }

    // @Transactional
    // public ProductDTO createProduct(ProductCreateUpdateDTO productCreateDTO) {

    //     if(productRepository.existsByAffiliateLink(productCreateDTO.getAffiliateLink())) {
    //         throw new RuntimeException("Product with affiliate link " + productCreateDTO.getAffiliateLink() + " already exists.");
    //     }
        
    //     Country country = countryService.getCountryByCode(productCreateDTO.getCountryCode());
    //     Category category = categoryService.getCategoryByName(productCreateDTO.getCategoryName());

    //     Product product = new Product();
    //     product.setPublicId(createPublicId());
    //     product.setName(productCreateDTO.getName());
    //     product.setDescription(productCreateDTO.getDescription());
    //     product.setCountry(country);
    //     product.setCurrentPrice(productCreateDTO.getCurrentPrice());
    //     product.setAffiliateLink(productCreateDTO.getAffiliateLink());
    //     product.setRating(productCreateDTO.getRating());
    //     product.setReviewCount(productCreateDTO.getReviewCount());
    //     product.setCategory(category);

    //     Product savedProduct = productRepository.save(product);

    //     PriceHistory priceHistory = new PriceHistory();
    //     priceHistory.setPrice(savedProduct.getCurrentPrice());
    //     priceHistory.setRecordedAt(dateUtil.getCurrentDateTime());
    //     priceHistory.setProduct(savedProduct);
    //     priceHistoryService.createPriceHistory(priceHistory);

    //     return mapToProductDTO(savedProduct);
    // }

    // @Transactional
    // public ProductDTO updateProduct(String publicId, ProductCreateUpdateDTO productUpdateDTO) {
    //     Product product = getProductByPublicId(publicId);

    //     Double oldPrice = product.getCurrentPrice();
    //     Country country = countryService.getCountryByCode(productUpdateDTO.getCountryCode());
    //     Category category = categoryService.getCategoryByName(productUpdateDTO.getCategoryName());

    //     // update product
    //     product.setName(productUpdateDTO.getName());
    //     product.setDescription(productUpdateDTO.getDescription());
    //     product.setCountry(country);
    //     product.setCurrentPrice(productUpdateDTO.getCurrentPrice());
    //     product.setAffiliateLink(productUpdateDTO.getAffiliateLink());
    //     product.setRating(productUpdateDTO.getRating());
    //     product.setReviewCount(productUpdateDTO.getReviewCount());
    //     product.setCategory(category);
    //     ZonedDateTime currentDateTime = dateUtil.getCurrentDateTime();
    //     product.setUpdatedAt(currentDateTime);
        
    //     Product savedProduct = productRepository.save(product);

    //     // create record price history if price changed
    //     if(!oldPrice.equals(productUpdateDTO.getCurrentPrice())) {
    //         PriceHistory priceHistory = new PriceHistory();
    //         priceHistory.setPrice(productUpdateDTO.getCurrentPrice());
    //         priceHistory.setRecordedAt(currentDateTime);
    //         priceHistory.setProduct(savedProduct);
    //         priceHistoryService.createPriceHistory(priceHistory);
    //     }

    //     return mapToProductDTO(savedProduct);
    // }

    // @Transactional
    // public void deleteProduct(String publicId) {
    //     Product product = getProductByPublicId(publicId);

    //     // prima elimino referenze al record eliminando i record da price history
    //     product.getPriceHistories().clear(); // oppure priceHistoryRepository.deleteByProduct(product);
        
    //     // poi posso procedere ad eliminare a eliminare il prodotto
    //     // productRepository.delete(getProductById(id));
    //     productRepository.deleteById(product.getId());
    // }

    // prodotti correlati in base ai prodotti menzionati dall'articolo
    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> findRelatedProductsByArticleAndCountry(Article article, int maxResults, String countryCode) {
        
        // raccogli publicId dei prodotti menzionati
        List<String> articleProdPublicIds = new ArrayList<>();
        for (Product product : article.getProducts()) {
            articleProdPublicIds.add(product.getPublicId());
        }
        if (articleProdPublicIds.isEmpty()) return List.of();
        
        // prodotti completi dal DB (per avere tag e categoria) - prendo solo i prodotti menzionati in articolo filtrati per country code
        List<Product> originalProducts = productMarketRepository.findByPublicIdsAndCountry(articleProdPublicIds, countryCode);
        if (originalProducts.isEmpty()) return List.of();

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

        return productMarketRepository.findRelatedProductsByCountry(
                articleProdPublicIds,
                new ArrayList<>(relatedTags),
                !relatedTags.isEmpty(),
                mainCategory,
                countryCode,
                maxResults
            ).stream()
            // .filter(p -> p.getCountry().getCode().equalsIgnoreCase(countryCode)) // qui puoi anche filtrare di nuovo per market, se la native non lo fa
            .map(this::mapToProductDetailsDTO)
            .collect(Collectors.toList());
    }

    // prodotti correlati in base al prodotto, la cui pagina e' stata aperta
    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> findRelatedProductsByProductAndCountry(ProductMarket productMarket, int maxResults, String countryCode) {
        // Tag del prodotto
        List<String> productMarketTags = productMarket.getProduct().getTags();
        List<String> tags = productMarketTags != null && !productMarketTags.isEmpty() ? productMarketTags : List.of();

        // Categoria del prodotto
        Category productMarketCategory = productMarket.getProduct().getCategory();
        String categoryName = productMarketCategory != null ? productMarketCategory.getName() : null;

        // Query per trovare prodotti correlati (escludendo quello attuale)
        return productMarketRepository.findRelatedProductsByCountry(
                List.of(productMarket.getPublicId()), // excludeIds
                tags,
                !tags.isEmpty(),
                categoryName,
                countryCode,
                maxResults
            ).stream()
            .map(this::mapToProductDetailsDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDetailsDTO> searchProducts(String q, int limit, String countryCode) {
        if (!StringUtils.hasText(q)) return List.of();

        String query = q.trim(); // Normalizza query (trim ecc.)
        int safeLimit = Math.max(1, Math.min(limit, 50)); // guard-rail

        // Semplice ricerca testuale su name/brand/description + tags
        List<ProductMarket> products = productMarketRepository.searchByTextAndCountry(query, safeLimit, countryCode);

        return products.stream()
            .map(this::mapToProductDetailsDTO)
            .collect(Collectors.toList());
    }
}
