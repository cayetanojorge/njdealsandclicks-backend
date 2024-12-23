package com.njdealsandclicks.newsletter;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {
    
    private final NewsletterService newsletterService;

    public NewsletterController(NewsletterService newsletterService) {
        this.newsletterService = newsletterService;
    }

    @GetMapping("/")
    public List<Newsletter> getAllSubscription() {
        return newsletterService.getAllNewsletterSubscription();
    }

    @GetMapping("/subscriptions")
    public Newsletter getSubscriptionByEmail(@RequestBody String email) {
        return newsletterService.getNewsletterSubscriptionByEmail(email);
    }

    @PostMapping("/subscribe")
    public Newsletter subscribeToNewsletter(@RequestBody Newsletter newsletter) {        
        return newsletterService.createNewsletterSubscription(newsletter);
    }
    
    @PutMapping("/updatesubscription")
    public Newsletter updateSubscription(@RequestBody Newsletter newsletter) {
        return newsletterService.updateNewsletterSubscription(newsletter);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscription(@PathVariable Long id) {
        newsletterService.deleteNewsletterSubscription(id);
    }

    // @DeleteMapping("/deletesubscription")
    // public void deleteSubscription(@RequestBody String email) {
    //     newsletterService.deleteSubscription(email);
    // }
    
}
