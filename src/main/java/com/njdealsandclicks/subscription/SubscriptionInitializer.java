package com.njdealsandclicks.subscription;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.njdealsandclicks.common.dbinitializer.EntityInitializer;
import com.njdealsandclicks.entityinitialized.EntityInitializedService;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionInitializer implements EntityInitializer {
    
    private static final String PREFIX_PUBLIC_ID = "sub_";

    private final SubscriptionRepository subscriptionRepository;
    private final EntityInitializedService entityInitializedService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final YamlService yamlService;

    @Override
    @NonNull
    public String getEntityName() {
        return "subscription";
    }

    @Override
    public String getYamlName() {
        return "subscriptions.yml";
    }

    @Override
    public String getInitializationVersion() {
        return "1.0"; 
    }

    @Override
    public int getExecutionOrder() {
        return 4;
    }

    @Override
    @Transactional
    public void initialize() {
        
        if (!entityInitializedService.needsInitialization(getEntityName(), getYamlName())) {
            // log.info("Skipping initialization for {}", getEntityName());
            return;
        }

        List<Subscription> allSubscriptions = yamlService.loadEntitiesFromYaml(
            "subscriptions.yml",
            Subscription.class,
            this::mapYamlToSubscription            
        );

        List<String> publicIds = createBatchPublicIdsV2(allSubscriptions.size());
        for(int i=0; i<allSubscriptions.size(); i++) {
            allSubscriptions.get(i).setPublicId(publicIds.get(i));
        }

        subscriptionRepository.saveAll(allSubscriptions);
        entityInitializedService.markAsInitialized(getEntityName(), getYamlName(), getInitializationVersion());
    }
    
    @SuppressWarnings("unchecked")
    private Subscription mapYamlToSubscription(Map<String, Object> data) {
        Subscription subscription = new Subscription();
        subscription.setPlanName((String) data.get("planName"));
        subscription.setDescription((String) data.get("description"));
        subscription.setPrice((Double) data.get("price"));
        subscription.setDurationInDays((Integer) data.get("durationInDays"));
        subscription.setMaxEmailsPerWeek((Integer) data.get("maxEmailsPerWeek"));
        subscription.setMaxTrackedProducts((Integer) data.get("maxTrackedProducts"));
        subscription.setMaxTrackedCategories((Integer) data.get("maxTrackedCategories"));
        subscription.setIsActive((Boolean) data.get("isActive"));
        Object featuresRaw = data.get("features");
        if (featuresRaw instanceof List) {
            subscription.setFeatures((List<String>) featuresRaw);
        } else {
            subscription.setFeatures(Collections.emptyList());
        }
        return subscription;
    }

    private List<String> createBatchPublicIdsV2(int nPublicIds) {
        return publicIdGeneratorService.generateBatchPublicIds(PREFIX_PUBLIC_ID, subscriptionRepository::filterAvailablePublicIds, nPublicIds);
    }
}
