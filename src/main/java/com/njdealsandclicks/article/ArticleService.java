package com.njdealsandclicks.article;

import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.article.ArticleCreateUpdateDTO;
import com.njdealsandclicks.dto.article.ArticleDTO;
import com.njdealsandclicks.product.ProductService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class ArticleService {
    
    private static final String PREFIX_PUBLIC_ID = "art_";

    private final ArticleRepository articleRepository;
    private final ProductService productService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;

    public ArticleService(ArticleRepository articleRepository, ProductService productService, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.articleRepository = articleRepository;
        this.productService = productService;
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
        articleDTO.setProductDTOs(productService.getAllProducts());
        return articleDTO;
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
    public Article getArticleById(UUID id) {
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
        return articleRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Article with slug " + slug + " not found"));
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

    // @Transactional
    // public void deleteArticle(String publicId) {
    //     Article currency = getArticleByPublicId(publicId);
    //     articleRepository.deleteById(currency.getId());
    // }

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
