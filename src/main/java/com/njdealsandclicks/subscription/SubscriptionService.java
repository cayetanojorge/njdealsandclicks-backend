package com.njdealsandclicks.subscription;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.subscription.SubscriptionCreateUpdateDTO;
import com.njdealsandclicks.dto.subscription.SubscriptionDTO;
import com.njdealsandclicks.util.DatabaseInitializationService;
import com.njdealsandclicks.util.PublicIdGenerator;

import jakarta.annotation.PostConstruct;

@Service
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;

    /* init db */
    private final DatabaseInitializationService databaseInitializationService;

    // // // @Value("${custom.subscriptions-file}")
    // // // private Resource subscriptionsResource;

    private final PublicIdGenerator publicIdGenerator;
    private final int MAX_ATTEMPTS = 3;
    private final String PREFIX_PUBLIC_ID = "sub_";

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PublicIdGenerator publicIdGenerator, DatabaseInitializationService databaseInitializationService) {
        this.subscriptionRepository = subscriptionRepository;
        this.publicIdGenerator = publicIdGenerator;
        this.databaseInitializationService = databaseInitializationService;
    }

    /* ************ initialize db ************ */
    @PostConstruct
    public void initializeSubscriptions() {
        // System.out.println("|- initializeSubscriptions() - Inizializzazione del database in corso...");
        List<Subscription> allSubscriptions = databaseInitializationService.loadEntitiesFromYaml(
            "subscriptions.yml",
            Subscription.class,
            this::mapYamlToSubscription
        );

        List<String> existingPlanNames = subscriptionRepository.findAllPlanNames();

        List<Subscription> subscriptionsToSave = allSubscriptions.stream()
            .filter(sub -> !existingPlanNames.contains(sub.getPlanName()))
            .collect(Collectors.toList());

        subscriptionRepository.saveAll(subscriptionsToSave);
    }

    private Subscription mapYamlToSubscription(Map<String, Object> data) {
        Subscription subscription = new Subscription();
        subscription.setPublicId(createPublicId());
        subscription.setPlanName((String) data.get("planName"));
        subscription.setDescription((String) data.get("description"));
        subscription.setPrice((Double) data.get("price"));
        subscription.setDurationInDays((Integer) data.get("durationInDays"));
        subscription.setMaxEmailsPerWeek((Integer) data.get("maxEmailsPerWeek"));
        subscription.setMaxTrackedProducts((Integer) data.get("maxTrackedProducts"));
        subscription.setMaxTrackedCategories((Integer) data.get("maxTrackedCategories"));
        subscription.setIsActive((Boolean) data.get("isActive"));
        return subscription;
    }
    /* ************************************************ */

    // // // @PostConstruct
    // // // public void loadSubscriptionsFromYaml() {
    // // //     Yaml yaml = new Yaml();
    // // //     try (InputStream inputStream = subscriptionsResource.getInputStream()) {
    // // //         Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);
    // // //         List<Map<String, Object>> subscriptions = data.get("subscriptions");
            
    // // //         subscriptions.forEach(subscriptionData -> {
    // // //             String planName = (String) subscriptionData.get("planName");
    // // //             if (!subscriptionRepository.existsByPlanName(planName)) {
    // // //                 Subscription subscription = new Subscription();
    // // //                 subscription.setPlanName(planName);
    // // //                 subscription.setDescription((String) subscriptionData.get("description"));
    // // //                 subscription.setPrice((Double) subscriptionData.get("price"));
    // // //                 subscription.setDurationInDays((Integer) subscriptionData.get("durationInDays"));
    // // //                 subscription.setMaxEmailsPerWeek((Integer) subscriptionData.get("maxEmailsPerWeek"));
    // // //                 subscription.setMaxTrackedProducts((Integer) subscriptionData.get("maxTrackedProducts"));
    // // //                 subscription.setMaxTrackedCategories((Integer) subscriptionData.get("maxTrackedCategories"));
    // // //                 subscription.setIsActive((Boolean) subscriptionData.get("isActive"));
    // // //                 subscriptionRepository.save(subscription);
    // // //             }
    // // //         });
    // // //     } catch (Exception e) {
    // // //         throw new RuntimeException("Error loading subscriptions from YAML", e);
    // // //     }
    // // // }

    private String createPublicId() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            List<String> publicIdBatch = publicIdGenerator.generatePublicIdBatch(PREFIX_PUBLIC_ID);
            List<String> existingIds = subscriptionRepository.findExistingPublicIds(publicIdBatch);
            List<String> uniqueIds = publicIdBatch.stream()
                                                  .filter(id -> !existingIds.contains(id))
                                                  .collect(Collectors.toList());
            if (!uniqueIds.isEmpty()) {
                return uniqueIds.get(0);
            }
        }
        throw new IllegalStateException("SubscriptionService - failed to generate unique publicId after " + MAX_ATTEMPTS + " batch attempts.");
    }

    private SubscriptionDTO mapToSubscriptionDTO(Subscription subscription) {
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setPublicId(subscription.getPublicId());
        subscriptionDTO.setPlanName(subscription.getPlanName());
        subscriptionDTO.setDescription(subscription.getDescription());
        subscriptionDTO.setFeatures(subscription.getFeatures());
        subscriptionDTO.setPrice(subscription.getPrice());
        subscriptionDTO.setPromotionalPrice(subscription.getPromotionalPrice());
        subscriptionDTO.setPromotionEndDate(subscription.getPromotionEndDate());
        subscriptionDTO.setDurationInDays(subscription.getDurationInDays());
        subscriptionDTO.setMaxEmailsPerWeek(subscription.getMaxEmailsPerWeek());
        subscriptionDTO.setMaxTrackedProducts(subscription.getMaxTrackedProducts());
        subscriptionDTO.setMaxTrackedCategories(subscription.getMaxTrackedCategories());
        subscriptionDTO.setIsActive(subscription.getIsActive());
        return subscriptionDTO;
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllSubscriptions() {
        // return subscriptionRepository.findAll();

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        return subscriptions.stream()
            .map(this::mapToSubscriptionDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionById(UUID id) {
        return subscriptionRepository.findById(id).orElseThrow(() -> new RuntimeException("Subscription with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionByPublicId(String publicId) {
        return subscriptionRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Subscription with publicId " + publicId + " not found"));
    }

    public SubscriptionDTO getSubscriptionDTOByPublicId(String publicId) {
        return mapToSubscriptionDTO(getSubscriptionByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Subscription getSubscriptionByPlanName(String planName) {
        return subscriptionRepository.findByPlanName(planName).orElseThrow(() -> new RuntimeException("Subscription with plan name " + planName + " not found"));
    }

    @Transactional
    public SubscriptionDTO createSubscription(SubscriptionCreateUpdateDTO subscriptionCreateDTO) {
        Subscription subscription = getSubscriptionByPlanName(subscriptionCreateDTO.getPlanName());
        if(subscription != null) {
            throw new RuntimeException("Subscription with name " + " already exists");
        }
        subscription = new Subscription();
        subscription.setPublicId(createPublicId());
        subscription.setPlanName(subscriptionCreateDTO.getPlanName());
        subscription.setDescription(subscriptionCreateDTO.getDescription());
        subscription.setFeatures(subscriptionCreateDTO.getFeatures());
        subscription.setPrice(subscriptionCreateDTO.getPrice());
        subscription.setPromotionalPrice(subscriptionCreateDTO.getPromotionalPrice());
        subscription.setPromotionEndDate(subscriptionCreateDTO.getPromotionEndDate());
        subscription.setDurationInDays(subscriptionCreateDTO.getDurationInDays());
        subscription.setMaxEmailsPerWeek(subscriptionCreateDTO.getMaxEmailsPerWeek());
        subscription.setMaxTrackedCategories(subscriptionCreateDTO.getMaxTrackedCategories());
        subscription.setMaxTrackedCategories(subscriptionCreateDTO.getMaxTrackedCategories());
        subscription.setIsActive(subscriptionCreateDTO.getIsActive());
        return mapToSubscriptionDTO(subscriptionRepository.save(subscription));
    }

    @Transactional
    public SubscriptionDTO updateSubscription(String publicId, SubscriptionCreateUpdateDTO subscriptionUpdateDTO) {
        Subscription subscription = getSubscriptionByPublicId(publicId);
        subscription.setPlanName(subscriptionUpdateDTO.getPlanName());
        subscription.setDescription(subscriptionUpdateDTO.getDescription());
        subscription.setFeatures(subscriptionUpdateDTO.getFeatures());
        subscription.setPrice(subscriptionUpdateDTO.getPrice());
        subscription.setPromotionalPrice(subscriptionUpdateDTO.getPromotionalPrice());
        subscription.setPromotionEndDate(subscriptionUpdateDTO.getPromotionEndDate());
        subscription.setDurationInDays(subscriptionUpdateDTO.getDurationInDays());
        subscription.setMaxEmailsPerWeek(subscriptionUpdateDTO.getMaxEmailsPerWeek());
        subscription.setMaxTrackedCategories(subscriptionUpdateDTO.getMaxTrackedCategories());
        subscription.setMaxTrackedCategories(subscriptionUpdateDTO.getMaxTrackedCategories());
        subscription.setIsActive(subscriptionUpdateDTO.getIsActive());
        return mapToSubscriptionDTO(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void deleteUser(String publicId) {
        Subscription subscription = getSubscriptionByPublicId(publicId);
        subscriptionRepository.deleteById(subscription.getId());
    }
}
