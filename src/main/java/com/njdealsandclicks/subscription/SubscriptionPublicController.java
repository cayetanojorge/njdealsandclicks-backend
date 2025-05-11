package com.njdealsandclicks.subscription;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.subscription.SubscriptionDTO;


@RestController
@RequestMapping("/api/public/subscription")
public class SubscriptionPublicController {
    
    private final SubscriptionService subscriptionService;

    public SubscriptionPublicController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/")
    public List<SubscriptionDTO> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }
}
