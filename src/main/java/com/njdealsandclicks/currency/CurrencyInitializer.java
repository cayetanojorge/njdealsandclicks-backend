package com.njdealsandclicks.currency;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.njdealsandclicks.common.dbinitializer.EntityInitializer;
import com.njdealsandclicks.entityinitialized.EntityInitializedService;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CurrencyInitializer implements EntityInitializer {

    private static final String PREFIX_PUBLIC_ID = "curren_";

    private final CurrencyRepository currencyRepository;
    private final EntityInitializedService entityInitializedService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final YamlService yamlService;


    @Override
    public String getEntityName() {
        return "currency";
    }

    @Override
    public String getYamlName() {
        return "currencies.yml";
    }
    
    @Override
    public String getInitializationVersion() {
        return "1.0"; 
    }

    @Override
    @Transactional
    public void initialize() {

        if (!entityInitializedService.needsInitialization(getEntityName(), getYamlName())) {
            // log.info("Skipping initialization for {}", getEntityName());
            return;
        }

        List<Currency> allCurrencies = yamlService.loadEntitiesFromYaml(
            "currencies.yml",
            Currency.class,
            this::mapYamlToCurrency
        );

        List<String> publicIds = createBatchPublicIdsV2(allCurrencies.size());
        for(int i=0; i<allCurrencies.size(); i++) {
            allCurrencies.get(i).setPublicId(publicIds.get(i));
        }

        currencyRepository.saveAll(allCurrencies);
        entityInitializedService.markAsInitialized(getEntityName(), getYamlName(), getInitializationVersion());
    }

    private Currency mapYamlToCurrency(Map<String, Object> data) {
        Currency currency = new Currency();
        currency.setCode((String) data.get("code"));
        currency.setName((String) data.get("name"));
        currency.setSymbol((String) data.get("symbol"));
        return currency;
    }

    private List<String> createBatchPublicIdsV2(int nPublicIds) {
        return publicIdGeneratorService.generateBatchPublicIds(PREFIX_PUBLIC_ID, currencyRepository::filterAvailablePublicIds, nPublicIds);
    }
    
}
