package com.njdealsandclicks.article;

import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.article.ArticleCreateUpdateDTO;
import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.dto.article.ArticleDetailsDTO;
import com.njdealsandclicks.dto.product.ProductDetailsDTO;
import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.product.ProductService;
import com.njdealsandclicks.productmarket.ProductMarketService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class ArticleService {
    
    private static final String PREFIX_PUBLIC_ID = "art_";

    private final ArticleRepository articleRepository;
    private final ProductService productService;
    private final ProductMarketService productMarketService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;

    public ArticleService(ArticleRepository articleRepository, ProductService productService, ProductMarketService productMarketService,
                        PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.articleRepository = articleRepository;
        this.productService = productService;
        this.productMarketService = productMarketService;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, articleRepository::filterAvailablePublicIds);
    }

    private ArticleDTO mapToArticleDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setPublicId(article.getPublicId());
        articleDTO.setSlug(article.getSlug());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setExcerpt(article.getExcerpt());
        articleDTO.setContent(article.getContent());
        articleDTO.setImageUrl(article.getImageUrl());
        articleDTO.setTags(article.getTags());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        articleDTO.setPublishedAt(article.getPublishedAt());
        articleDTO.setReadingTimeMinutes(article.getContent().split(" ").length / 200);
        return articleDTO;
    }

    private ArticleDetailsDTO mapToArticleDetailsDTO(Article article, String countryCode) {
        ArticleDetailsDTO articleDetailsDTO = new ArticleDetailsDTO();
        articleDetailsDTO.setPublicId(article.getPublicId());
        articleDetailsDTO.setSlug(article.getSlug());
        articleDetailsDTO.setTitle(article.getTitle());
        articleDetailsDTO.setExcerpt(article.getExcerpt());
        articleDetailsDTO.setContent(article.getContent());
        articleDetailsDTO.setImageUrl(article.getImageUrl());
        articleDetailsDTO.setTags(article.getTags());
        articleDetailsDTO.setUpdatedAt(article.getUpdatedAt());
        articleDetailsDTO.setPublishedAt(article.getPublishedAt());
        articleDetailsDTO.setReadingTimeMinutes(article.getContent().split(" ").length / 200);

        List<Product> products = article.getProducts();
        if (products == null || products.isEmpty()) {
            articleDetailsDTO.setProductDetailsDTOs(List.of());
            return articleDetailsDTO;
        }
        List<String> productPublicIds = products.stream().map(Product::getPublicId).toList();
        List<ProductDetailsDTO> productDetailsDTOs = productMarketService.findByProductAndCountry(productPublicIds, countryCode);
        articleDetailsDTO.setProductDetailsDTOs(productDetailsDTOs);

        return articleDetailsDTO;
    }

    // for inter purpose
    @Transactional(readOnly = true)
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
            .map(this::mapToArticleDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getAllArticlesIsDeletedFalseAndIsPublishedTrue() {
        List<Article> articles = articleRepository.findAllByIsDeletedFalseAndIsPublishedTrue();
        return articles.stream()
            .map(this::mapToArticleDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Article getArticleById(@NonNull UUID id) {
        return articleRepository.findById(id).orElseThrow(() -> new RuntimeException("Article with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Article getArticleByPublicId(String publicId) {
        return articleRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Article with publicId " + publicId + " not found"));
    }

    public ArticleDTO getArticleDTOByPublicId(String publicId) {
        return mapToArticleDTO(getArticleByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Article getArticleBySlug(String slug) {
        // return articleRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Article with slug " + slug + " not found"));
        return articleRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Article getArticleWithProductsBySlug(String slug) {
        return articleRepository.findWithProductsBySlug(slug);
    }    

    @Transactional(readOnly = true)
    public Article getArticleBySlugAndCountry(String slug, String countryCode) {
        Article article = getArticleBySlug(slug);
        List<Product> products = articleRepository.findProductsBySlugAndCountry(slug, countryCode);
        article.setProducts(products);
        return article;
    }

    @Transactional(readOnly = true)
    public ArticleDTO getArticleDTOBySlug(String slug) {
        // return articleRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Article with slug " + slug + " not found"));
        return mapToArticleDTO(getArticleBySlug(slug));
    }
    
    @Transactional(readOnly = true)
    public ArticleDetailsDTO getArticleDTOBySlugAndCountry(String slug, String countryCode) {
        return mapToArticleDetailsDTO(getArticleBySlugAndCountry(slug, countryCode), countryCode);
    }

    @Transactional
    public ArticleDTO createArticle(ArticleCreateUpdateDTO articleCreateDTO) {
        String slug = titleToSlug(articleCreateDTO.getTitle());
        
        Article article = getArticleBySlug(slug);
        if(article != null) {
            throw new RuntimeException("Article with slug " + slug + " already exists");
        }
        article = new Article();
        article.setPublicId(createPublicId());
        article.setTitle(articleCreateDTO.getTitle());
        article.setSlug(slug);
        article.setExcerpt(articleCreateDTO.getExcerpt());
        article.setContent(articleCreateDTO.getContent());
        article.setImageUrl(articleCreateDTO.getImageUrl());
        article.setTags(articleCreateDTO.getTags());
        article.setIsPublished(articleCreateDTO.getIsPublished());
        
        ZonedDateTime now = dateUtil.getCurrentDateTime();
        article.setCreatedAt(now);
        if(articleCreateDTO.getIsPublished()) {
            article.setPublishedAt(now);
        }

        article.setProducts(productService.getProductsByPublicIds(articleCreateDTO.getProductPublicIds()));

        return mapToArticleDTO(articleRepository.save(article));
    }

    @Transactional
    public ArticleDTO updateArticle(String publicId, ArticleCreateUpdateDTO articleUpdateDTO) {
        Article article = getArticleByPublicId(publicId);
        article.setTitle(articleUpdateDTO.getTitle());
        article.setSlug(titleToSlug(articleUpdateDTO.getTitle()));
        article.setExcerpt(articleUpdateDTO.getExcerpt());
        article.setContent(articleUpdateDTO.getContent());
        article.setImageUrl(articleUpdateDTO.getImageUrl());
        article.setTags(articleUpdateDTO.getTags());
        article.setIsPublished(articleUpdateDTO.getIsPublished());

        ZonedDateTime now = dateUtil.getCurrentDateTime();
        article.setUpdatedAt(now);
        if(articleUpdateDTO.getIsPublished()) {
            article.setPublishedAt(now);
        }

        article.setProducts(productService.getProductsByPublicIds(articleUpdateDTO.getProductPublicIds()));
        return mapToArticleDTO(articleRepository.save(article));
    }

    @Transactional
    public void softDeleteArticle(String publicId) {
        Article article = getArticleByPublicId(publicId);
        article.setIsDeleted(true);
        article.setDeletedAt(dateUtil.getCurrentDateTime());
    }

    @Transactional
    public void deleteArticle(String publicId) {
        Article article = getArticleByPublicId(publicId);
        UUID id = Objects.requireNonNull(article.getId(), "Article id must not be null");
        articleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> findRelatedArticlesByArticle(Article article, int maxResults) {
        // Raccogli categoria da uno dei prodotti,
        // futuro: considera piu' cagetorie, dato i prodotti in lista
        String mainCategory = article.getProducts().stream()
            .map(p -> p.getCategory() != null ? p.getCategory().getName() : null)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);

        return articleRepository.findRelatedArticles(
                article.getPublicId(),
                article.getTags() != null ? article.getTags() : List.of(),
                mainCategory,
                maxResults
            ).stream()
            .map(this::mapToArticleDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> findArticlesThatMentionProduct(String productPublicId) {
        return articleRepository.findPublishedArticlesByProductPublicId(productPublicId)
            .stream()
            .map(this::mapToArticleDTO)
            .collect(Collectors.toList());
    }

    private String titleToSlug(String title) {
        return Normalizer.normalize(title, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "") // rimuovi accenti
            .toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "") // rimuovi caratteri non validi
            .replaceAll("\\s+", "-") // sostituisci spazi con -
            .replaceAll("-+", "-") // rimuovi doppio trattino
            .replaceAll("^-|-$", ""); // rimuovi trattini iniziali/finali
    }

    // // // possibile usare in createArticle, se esiste gia' slug:
    // // // slug = "guida-ai-migliori-tv-oled-del-2025"
    // // // allora ne creo altro
    // // // slug = "guida-ai-migliori-tv-oled-del-2025-2"
    // // private String generateUniqueSlug(String baseSlug) {
    // //     String slug = baseSlug;
    // //     int suffix = 2;
    // //     while (articleRepository.findBySlug(slug).isPresent()) {
    // //         slug = baseSlug + "-" + suffix;
    // //         suffix++;
    // //     }
    // //     return slug;
    // // }
}
