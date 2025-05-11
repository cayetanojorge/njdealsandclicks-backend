package com.njdealsandclicks.newsletter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.category.CategoryDTO;
import com.njdealsandclicks.dto.newsletter.NewsletterCreateUpdateDTO;
import com.njdealsandclicks.dto.newsletter.NewsletterDTO;
import com.njdealsandclicks.dto.product.ProductDTO;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/public/newsletter")
public class NewsletterPublicController {
    
    private final NewsletterService newsletterService;

    public NewsletterPublicController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @GetMapping("/")
    public List<NewsletterDTO> getAllSubscription() {
        return newsletterService.getAllNewsletters();
    }

    @GetMapping("/email")
    public Newsletter getSubscriptionByEmail(@RequestBody String userEmail) {
    return newsletterService.getNewsletterByUserEmail(userEmail);
    }

    @PostMapping("/create")
    public NewsletterDTO subscribeToNewsletter(@RequestBody NewsletterCreateUpdateDTO newsletterCreateDTO) {        
        return newsletterService.createNewsletterSubscription(newsletterCreateDTO);
    }
    
    @PutMapping("/update")
    public NewsletterDTO updateNewsletter(@RequestBody NewsletterCreateUpdateDTO newsletterUpdateDTO) {
        return newsletterService.updateNewsletterSubscription(newsletterUpdateDTO);
    }

    @DeleteMapping("/{publicId}")
    public void deleteNewsletter(@PathVariable String publicId) {
        newsletterService.deleteNewsletter(publicId);
    }

    // @DeleteMapping("/deletesubscription")
    // public void deleteSubscription(@RequestBody String email) {
    //     newsletterService.deleteSubscription(email);
    // }

    @PostMapping("/{userPublicId}/add-favorite-product")
    public NewsletterDTO addFavoriteProduct(@PathVariable String userPublicId, @RequestBody ProductDTO productDTO) {
        return newsletterService.addFavoriteProduct(userPublicId, productDTO);
    }

    @DeleteMapping("/{userPublicId}/del-favorite-product/{productPublicId}")
    public void deleteFavoriteProduct(@PathVariable String userPublicId, @PathVariable String productPublicId) {
        newsletterService.removeFavoriteProduct(userPublicId, productPublicId);
    }
    
    @PostMapping("/{userPublicId}/add-favorite-category")
    public NewsletterDTO addFavoriteCategory(@PathVariable String userPublicId, @RequestBody CategoryDTO categoryDTO) {
        return newsletterService.addFavoriteCategory(userPublicId, categoryDTO);
    }
    
    @DeleteMapping("/{userPublicId}/del-favorite-category/{categoryPublicId}")
    public void deleteFavoriteCategory(@PathVariable String userPublicId, @PathVariable String categoryPublicId) {
        newsletterService.removeFavoriteCategory(userPublicId, categoryPublicId);
    }
}
