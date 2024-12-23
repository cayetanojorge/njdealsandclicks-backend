package com.njdealsandclicks.newsletter;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class NewsletterService {
    
    private final NewsletterRepository newsletterRepository;

    public NewsletterService(NewsletterRepository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    @Transactional(readOnly = true)
    public List<Newsletter> getAllNewsletterSubscription() {
        return newsletterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterSubscription(Long id) {
        return newsletterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription with " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Newsletter getNewsletterSubscriptionByEmail(String email) {
        return newsletterRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Subscription not found for email: " + email));
    }

    @Transactional
    public Newsletter createNewsletterSubscription(Newsletter newsletter) {
        return newsletterRepository.save(newsletter);
    }

    @Transactional
    public Newsletter updateNewsletterSubscription(Newsletter newsletterDetails) {//boolean generalSubscription, Product product, List<Long> categoryIds) {
        Newsletter newsLetter = getNewsletterSubscriptionByEmail(newsletterDetails.getUser().getEmail());

        newsLetter.getUser().setName(newsletterDetails.getUser().getName());
        newsLetter.setGeneralSubsription(newsletterDetails.isGeneralSubsription());
        newsLetter.setProduct(newsletterDetails.getProduct());
        newsLetter.setCategories(newsletterDetails.getCategories());

        return newsletterRepository.save(newsLetter);
    }

    @Transactional
    public void deleteNewsletterSubscription(Long id) {
        // metodo 1
        Newsletter newsletter = getNewsletterSubscription(id);
        newsletter.getCategories().clear(); // Svuota la lista di categorie associate (rimuove dalla tabella di join)
        
        // metodo 2
        // newsletterRepository.deleteNewsletterCategories(id);

        newsletterRepository.deleteById(id);
    }

    // public void deleteSubscription(String email) {
    //     newsletterRepository.deleteById(getNewsletterSubscriptionByEmail(email).getId());
    // }
}
