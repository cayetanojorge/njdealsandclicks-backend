package com.njdealsandclicks.currency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.currency.CurrencyCreateUpdateDTO;
import com.njdealsandclicks.dto.currency.CurrencyDTO;
import com.njdealsandclicks.util.DatabaseInitializationService;
import com.njdealsandclicks.util.PublicIdGeneratorService;

import jakarta.annotation.PostConstruct;

@Service
public class CurrencyService {

    private static final int MAX_ATTEMPTS = 3; // n massimo di tentativi di batch per generare publicId
    private static final String PREFIX_PUBLIC_ID = "curren_";

    private final CurrencyRepository currencyRepository;
    private final DatabaseInitializationService databaseInitializationService;
    private final PublicIdGeneratorService publicIdGeneratorService;
    

    public CurrencyService(CurrencyRepository currencyRepository, DatabaseInitializationService databaseInitializationService, PublicIdGeneratorService publicIdGeneratorService) {
        this.currencyRepository = currencyRepository;
        this.databaseInitializationService = databaseInitializationService;
        this.publicIdGeneratorService = publicIdGeneratorService;
    }

    @PostConstruct
    private void initializeCurrencies() {
        List<Currency> allCurrencies = databaseInitializationService.loadEntitiesFromYaml(
            "currencies.yml",
            Currency.class,
            this::mapYamlToCurrency
        );

        List<String> publicIds = getNPublicIds(allCurrencies.size());
        for(int i=0; i<allCurrencies.size(); i++) {
            allCurrencies.get(i).setPublicId(publicIds.get(i));
        }

        currencyRepository.saveAll(allCurrencies);
    }

    private Currency mapYamlToCurrency(Map<String, Object> data) {
        Currency currency = new Currency();
        currency.setCode((String) data.get("code"));
        currency.setName((String) data.get("name"));
        currency.setSymbol((String) data.get("symbol"));
        return currency;
    }

    private List<String> getNPublicIds(int nPublicIds) {
        List<String> retNpublicIds = new ArrayList<>();
        while (retNpublicIds.size() < nPublicIds) {
            List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID, nPublicIds);
            List<String> existingIds = currencyRepository.findExistingPublicIds(publicIdBatch);
            List<String> uniqueIds = publicIdBatch.stream()
                                                    .filter(id -> !existingIds.contains(id))
                                                    .collect(Collectors.toList());
            retNpublicIds.addAll(uniqueIds);
        }
        return retNpublicIds;
    }

    private String createPublicId() {
        // int batchSize = publicIdGeneratorService.INITIAL_BATCH_SIZE; 
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            // Genera un batch di PublicId
            List<String> publicIdBatch = publicIdGeneratorService.generatePublicIdBatch(PREFIX_PUBLIC_ID);

            // Verifica quali ID sono già presenti nel database
            List<String> existingIds = currencyRepository.findExistingPublicIds(publicIdBatch);

            // Filtra gli ID univoci
            List<String> uniqueIds = publicIdBatch.stream()
                                                  .filter(id -> !existingIds.contains(id))
                                                  .collect(Collectors.toList());

            // Se esiste almeno un ID univoco, lo restituisce
            if(!uniqueIds.isEmpty()) {
                return uniqueIds.get(0);
            }
        }
        throw new IllegalStateException("CategoryService - failed to generate unique publicId after " + MAX_ATTEMPTS + " batch attempts.");
    }

    private CurrencyDTO mapToCurrencyDTO(Currency currency) {
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.setPublicId(currency.getPublicId());
        currencyDTO.setCode(currency.getCode());
        currencyDTO.setName(currency.getName());
        currencyDTO.setSymbol(currency.getSymbol());
        return currencyDTO;
    }

    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
            .map(this::mapToCurrencyDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Currency getCurrencyById(UUID id) {
        return currencyRepository.findById(id).orElseThrow(() -> new RuntimeException("Currency with id " + id + " not found"));
    }
    
    @Transactional(readOnly = true)
    public Currency getCurrencyByPublicId(String publicId) {
        return currencyRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Currency with publicId " + publicId + " not found"));
    }

    public CurrencyDTO getCurrencyDTOByPublicId(String publicId) {
        return mapToCurrencyDTO(getCurrencyByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Currency getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Currency with code " + code + " not found"));
    }

    @Transactional
    public CurrencyDTO createCurrency(CurrencyCreateUpdateDTO currencyCreateDTO) {
        Currency currency = getCurrencyByCode(currencyCreateDTO.getCode());
        if(currency != null) {
            throw new RuntimeException("Currency with code " + currencyCreateDTO.getCode() + " already exists");
        }
        currency = new Currency();
        currency.setPublicId(createPublicId());
        currency.setCode(currencyCreateDTO.getCode());
        currency.setName(currencyCreateDTO.getName());
        currency.setSymbol(currencyCreateDTO.getSymbol());
        return mapToCurrencyDTO(currencyRepository.save(currency));
    }

    @Transactional
    public CurrencyDTO updateCurrency(String publicId, CurrencyCreateUpdateDTO currencyUpdateDTO) {
        Currency currency = getCurrencyByPublicId(publicId);
        currency.setCode(currencyUpdateDTO.getCode());
        currency.setName(currencyUpdateDTO.getName());
        currency.setSymbol(currencyUpdateDTO.getSymbol());
        return mapToCurrencyDTO(currencyRepository.save(currency));
    }

    @Transactional
    public void deleteCurrency(String publicId) {
        Currency currency = getCurrencyByPublicId(publicId);
        currencyRepository.deleteById(currency.getId());
    }
}
