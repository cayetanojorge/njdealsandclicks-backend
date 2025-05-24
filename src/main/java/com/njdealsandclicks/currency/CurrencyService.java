package com.njdealsandclicks.currency;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.currency.CurrencyCreateUpdateDTO;
import com.njdealsandclicks.dto.currency.CurrencyDTO;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class CurrencyService {

    private static final String PREFIX_PUBLIC_ID = "curren_";

    private final CurrencyRepository currencyRepository;
    private final PublicIdGeneratorService publicIdGeneratorService;
    

    public CurrencyService(CurrencyRepository currencyRepository, PublicIdGeneratorService publicIdGeneratorService) {
        this.currencyRepository = currencyRepository;
        this.publicIdGeneratorService = publicIdGeneratorService;
    }
    
    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, currencyRepository::filterAvailablePublicIds);
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
