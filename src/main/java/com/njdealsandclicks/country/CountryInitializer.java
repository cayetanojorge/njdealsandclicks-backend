package com.njdealsandclicks.country;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.njdealsandclicks.common.dbinitializer.EntityInitializer;
import com.njdealsandclicks.currency.CurrencyService;
import com.njdealsandclicks.entityinitialized.EntityInitializedService;
import com.njdealsandclicks.util.PublicIdGeneratorService;
import com.njdealsandclicks.util.YamlService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CountryInitializer implements EntityInitializer {

    private static final String PREFIX_PUBLIC_ID = "country_";

    private final CountryRepository countryRepository;
    private final CurrencyService currencyService;
    private final EntityInitializedService entityInitializedService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    private final YamlService yamlService;


    @Override
    public String getEntityName() {
        return "country";
    }

    @Override
    public String getYamlName() {
        return "countries.yml";
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

        List<Country> allCountries = yamlService.loadEntitiesFromYaml(
            getYamlName(),
            Country.class,
            this::mapYamlToCountry
        );

        List<String> publicIds = createBatchPublicIdsV2(allCountries.size());
        for(int i=0; i<allCountries.size(); i++) {
            allCountries.get(i).setPublicId(publicIds.get(i));
        }

        countryRepository.saveAll(allCountries);
        entityInitializedService.markAsInitialized(getEntityName(), getYamlName(), getInitializationVersion());
    }

    private Country mapYamlToCountry(Map<String, Object> data) {
        Country country = new Country();
        country.setCode((String) data.get("code"));
        country.setName((String) data.get("name"));
        country.setLocale((String) data.get("locale"));
        country.setTimezone((String) data.get("timezone"));
        country.setFlagEmoji((String) data.get("flagEmoji"));
        country.setCurrency(currencyService.getCurrencyByCode((String) data.get("currencyCode")));
        return country;
    }

    private List<String> createBatchPublicIdsV2(int nPublicIds) {
        return publicIdGeneratorService.generateBatchPublicIds(PREFIX_PUBLIC_ID, countryRepository::filterAvailablePublicIds, nPublicIds);
    }
    
}
