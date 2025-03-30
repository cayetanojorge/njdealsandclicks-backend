package com.njdealsandclicks.newsletter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.product.Product;
import com.njdealsandclicks.product.ProductService;
import com.njdealsandclicks.user.User;
import com.njdealsandclicks.user.UserService;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.category.Category;
import com.njdealsandclicks.category.CategoryService;
import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.dto.newsletter.NewsletterCreateUpdateDTO;
import com.njdealsandclicks.dto.newsletter.NewsletterDTO;
import com.njdealsandclicks.dto.product.ProductDTO;



@Service
public class NewsletterService {
    
    private static final int MAX_ATTEMPTS = 3;
    private static final String PREFIX_PUBLIC_ID = "news_";

    private final NewsletterRepository newsletterRepository;
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;

    
    public NewsletterService(NewsletterRepository newsletterRepository, UserService userService, ProductService productService,
                                CategoryService categoryService, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.newsletterRepository = newsletterRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        // int batchSize = publicIdGeneratorService.INITIAL_BATCH_SIZE; 
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Genera un batch di PublicId
            List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID);

            // Verifica quali ID sono già presenti nel database
            List<String> existingIds = newsletterRepository.findExistingPublicIds(publicIdBatch);

            // Filtra gli ID univoci
            List<String> uniqueIds = publicIdBatch.stream()
                                                  .filter(id -> !existingIds.contains(id))
                                                  .collect(Collectors.toList());

            // Se esiste almeno un ID univoco, lo restituisce
            if (!uniqueIds.isEmpty()) {
                return uniqueIds.get(0);
            }
        }

        throw new IllegalStateException("NewsletterService - failed to generate unique publicId after " + MAX_ATTEMPTS + " batch attempts.");
    }

    private NewsletterDTO mapToNewsletterDTO(Newsletter newsletter) {
        NewsletterDTO newsletterDTO = new NewsletterDTO();
        newsletterDTO.setPublicId(newsletter.getPublicId());
        newsletterDTO.setUserDTO(userService.getUserDTOByPublicId(newsletter.getUser().getPublicId()));
        newsletterDTO.setGeneralNewsletter(newsletter.getGeneralNewsletter());
        newsletterDTO.setProductDTOs(productService.productsToProductDTOs(newsletter.getProducts()));
        newsletterDTO.setCategoryDTOs(categoryService.categoriesToCategoryDTOs(newsletter.getCategories()));
        return newsletterDTO;
    }

    @Transactional(readOnly = true)
    public List<NewsletterDTO> getAllNewsletters() {
        List<Newsletter> newsletters = newsletterRepository.findAll();
        return newsletters.stream()
            .map(this::mapToNewsletterDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterById(UUID id) {
        return newsletterRepository.findById(id).orElseThrow(() -> new RuntimeException("Subscription with " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterByPublicId(String publicId) {
        return newsletterRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Newsletter with publicId " + publicId + " not found"));
    }

    public NewsletterDTO getNewsletterDTOByPublicId(String publicId) {
        return mapToNewsletterDTO(getNewsletterByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterByUserPublicId(String userPublicId) {
        return newsletterRepository.findByUserPublicId(userPublicId).orElseThrow(() -> new RuntimeException("Newsletter not found for user with publicId: " + userPublicId));
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterByUserEmail(String userEmail) {
        return newsletterRepository.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("Newsletter not found for user with email: " + userEmail));
    }

    @Transactional
    public NewsletterDTO createNewsletterSubscription(NewsletterCreateUpdateDTO newsletterCreateDTO) {
        /* voglio in caso esistesse già, di aggiornare */
        Newsletter newsletter = getNewsletterByUserEmail(newsletterCreateDTO.getUserCreateUpdateDTO().getEmail());
        if(newsletter != null) {
            return updateNewsletterSubscription(newsletterCreateDTO);
        }
        newsletter = new Newsletter();
        newsletter.setPublicId(createPublicId());
        newsletter.setUser(userService.getUserByPublicId(userService.createUser(newsletterCreateDTO.getUserCreateUpdateDTO()).getPublicId()));
        if(newsletterCreateDTO.getGeneralNewsletter() != null) {
            newsletter.setGeneralNewsletter(newsletterCreateDTO.getGeneralNewsletter());
        }

        List<String> productPublicIds = new ArrayList<>();
        for(ProductDTO p : newsletterCreateDTO.getProductDTOs()) {
            productPublicIds.add(p.getPublicId());
        }
        newsletter.setProducts(productService.getProductsByPublicIds(productPublicIds));

        List<String> categoryPublicIds = new ArrayList<>();
        for(CategoryDTO c : newsletterCreateDTO.getCategoryDTOs()) {
            categoryPublicIds.add(c.getPublicId());
        }
        newsletter.setCategories(categoryService.getCategoriesByPublicIds(categoryPublicIds));
        return mapToNewsletterDTO(newsletterRepository.save(newsletter));
    }

    @Transactional
    public NewsletterDTO updateNewsletterSubscription(NewsletterCreateUpdateDTO newsletterUpdateDTO) {
        Newsletter newsLetter = getNewsletterByUserEmail(newsletterUpdateDTO.getUserCreateUpdateDTO().getEmail());
        if(newsletterUpdateDTO.getGeneralNewsletter() != null) {
            newsLetter.setGeneralNewsletter(newsletterUpdateDTO.getGeneralNewsletter());
        }

        // TODO controlli che non eccediamo limite del piano dell' utente
        if(newsletterUpdateDTO.getProductDTOs() != null) {
            List<String> productPublicIds = new ArrayList<>();
            for(ProductDTO p : newsletterUpdateDTO.getProductDTOs()) {
                productPublicIds.add(p.getPublicId());
            }
            newsLetter.setProducts(productService.getProductsByPublicIds(productPublicIds));
        }
        if(newsletterUpdateDTO.getCategoryDTOs() != null) {
            List<String> categoryPublicIds = new ArrayList<>();
            for(CategoryDTO c : newsletterUpdateDTO.getCategoryDTOs()) {
                categoryPublicIds.add(c.getPublicId());
            }
            newsLetter.setProducts(productService.getProductsByPublicIds(categoryPublicIds));
        }

        newsLetter.setUpdatedAt(dateUtil.getCurrentDateTime());
        return mapToNewsletterDTO(newsletterRepository.save(newsLetter));
    }

    @Transactional
    public void deleteNewsletter(String publicId) {
        // metodo 1
        Newsletter newsletter = getNewsletterByPublicId(publicId);
        newsletter.getProducts().clear();
        newsletter.getCategories().clear(); // Svuota la lista di categorie associate (rimuove dalla tabella di join)
        
        // metodo 2
        // newsletterRepository.deleteNewsletterCategories(id);

        newsletterRepository.deleteById(newsletter.getId());
    }

    // public void deleteSubscription(String email) {
    //     newsletterRepository.deleteById(getNewsletterSubscriptionByEmail(email).getId());
    // }

    @Transactional
    public NewsletterDTO addFavoriteProduct(String userPublicId, ProductDTO productDTO) {
        User user = userService.getUserByPublicId(userPublicId);
        Newsletter newsletter = getNewsletterByUserPublicId(userPublicId);

        if(newsletter.getProducts().size() >= user.getSubscription().getMaxTrackedProducts()) {
            throw new RuntimeException("Because of your plan " + user.getSubscription().getPlanName() + " cannot add more than "  + user.getSubscription().getMaxTrackedProducts() + " products to favorites.");
        }

        newsletter.getProducts().add(productService.getProductByPublicId(productDTO.getPublicId()));
        return mapToNewsletterDTO(newsletterRepository.save(newsletter));
    }

    @Transactional
    public void removeFavoriteProduct(String userPublicId, String productPublicId) {
        Newsletter newsletter = getNewsletterByUserPublicId(userPublicId);
        Product productToRemove = newsletter.getProducts().stream()
            .filter(product -> product.getId().equals(productService.getProductByPublicId(productPublicId).getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Product with publicId " + productPublicId + " not found in favorites of newsletter with publicId " + newsletter.getPublicId()));
        
        newsletter.getProducts().remove(productToRemove);
        newsletterRepository.save(newsletter);
    }

    @Transactional
    public NewsletterDTO addFavoriteCategory(String userPublicId, CategoryDTO categoryDTO) {
        User user = userService.getUserByPublicId(userPublicId);
        Newsletter newsletter = getNewsletterByUserPublicId(userPublicId);

        if(newsletter.getCategories().size() >= user.getSubscription().getMaxTrackedCategories()) {
            throw new RuntimeException("Because of your plan " + user.getSubscription().getPlanName() + " cannot add more than "  + user.getSubscription().getMaxTrackedProducts() + " categories to favorites.");
        }

        newsletter.getCategories().add(categoryService.getCategoryByPublicId(categoryDTO.getPublicId()));
        return mapToNewsletterDTO(newsletterRepository.save(newsletter));
    }

    @Transactional
    public void removeFavoriteCategory(String userPublicId, String categoryPublicId) {
        Newsletter newsletter = getNewsletterByUserPublicId(userPublicId);
        Category categoryToRemove = newsletter.getCategories().stream()
            .filter(category -> category.getId().equals(categoryService.getCategoryByPublicId(categoryPublicId).getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Category with publicId " + categoryPublicId + " not found in favorites of newsletter with publicId " + newsletter.getPublicId()));
        
        newsletter.getCategories().remove(categoryToRemove);
        newsletterRepository.save(newsletter);
    }
}
