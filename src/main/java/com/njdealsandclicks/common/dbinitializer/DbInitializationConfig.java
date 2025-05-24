package com.njdealsandclicks.common.dbinitializer;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.njdealsandclicks.category.CategoryInitializer;
import com.njdealsandclicks.currency.CurrencyInitializer;
import com.njdealsandclicks.subscription.SubscriptionInitializer;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
public class DbInitializationConfig {
    
    private final CategoryInitializer categoryInitializer;
    private final CurrencyInitializer currencyInitializer;
    private final SubscriptionInitializer subscriptionInitializer;
    
    @Bean
    public List<EntityInitializer> entityInitializers() {
        return List.of(
            categoryInitializer,
            currencyInitializer,
            subscriptionInitializer
        );
    }
}