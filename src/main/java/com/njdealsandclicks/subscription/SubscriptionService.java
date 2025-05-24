package com.njdealsandclicks.subscription;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.subscription.SubscriptionCreateUpdateDTO;
import com.njdealsandclicks.dto.subscription.SubscriptionDTO;
import com.njdealsandclicks.util.DateUtil;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class SubscriptionService {
    
    private static final String PREFIX_PUBLIC_ID = "sub_";

    private final SubscriptionRepository subscriptionRepository;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final DateUtil dateUtil;

    
    public SubscriptionService(SubscriptionRepository subscriptionRepository, PublicIdGeneratorService publicIdGeneratorService, DateUtil dateUtil) {
        this.subscriptionRepository = subscriptionRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
        this.dateUtil = dateUtil;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicIdV2(PREFIX_PUBLIC_ID, subscriptionRepository::filterAvailablePublicIds);
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
            throw new RuntimeException("Subscription with planName " + subscriptionCreateDTO.getPlanName() + " already exists");
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
        subscription.setUpdatedAt(dateUtil.getCurrentDateTime());
        return mapToSubscriptionDTO(subscriptionRepository.save(subscription));
    }

    @Transactional
    public void deleteSubscription(String publicId) {
        Subscription subscription = getSubscriptionByPublicId(publicId);
        subscriptionRepository.deleteById(subscription.getId());
    }
}
