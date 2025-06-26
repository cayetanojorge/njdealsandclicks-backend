package com.njdealsandclicks.country;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.currency.Currency;
import com.njdealsandclicks.currency.CurrencyService;
import com.njdealsandclicks.dto.country.CountryCreateUpdateDTO;
import com.njdealsandclicks.dto.country.CountryDTO;
import com.njdealsandclicks.util.PublicIdGeneratorService;

@Service
public class CountryService {
    
    private static final String PREFIX_PUBLIC_ID = "country_";

    private final CountryRepository countryRepository;
    private final CurrencyService currencyService;
    private final PublicIdGeneratorService publicIdGeneratorService;


    public CountryService(CountryRepository countryRepository, CurrencyService currencyService, PublicIdGeneratorService publicIdGeneratorService) {
        this.countryRepository = countryRepository;
        this.currencyService = currencyService;
        this.publicIdGeneratorService = publicIdGeneratorService;
    }

    private String createPublicId() {
        return publicIdGeneratorService.generateSinglePublicId(PREFIX_PUBLIC_ID, countryRepository::filterAvailablePublicIds);
    }

    private CountryDTO mapToCountryDTO(Country country) {
        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setPublicId(country.getPublicId());
        countryDTO.setCode(country.getCode());
        countryDTO.setName(country.getName());
        countryDTO.setLanguage(country.getLanguage());
        countryDTO.setLocale(country.getLocale());
        countryDTO.setTimezone(country.getTimezone());
        countryDTO.setFlagEmoji(country.getFlagEmoji());
        countryDTO.setCurrencyDTO(currencyService.getCurrencyDTOByPublicId(country.getCurrency().getPublicId()));
        return countryDTO;
    }

    @Transactional(readOnly = true)
    public List<CountryDTO> getAllCountries() {
        List<Country> countries = countryRepository.findAll();
        return countries.stream()
            .map(this::mapToCountryDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Country getCountryById(UUID id) {
        return countryRepository.findById(id).orElseThrow(() -> new RuntimeException("Country with id " + id + " not found"));
    }
    
    @Transactional(readOnly = true)
    public Country getCountryByPublicId(String publicId) {
        return countryRepository.findByPublicId(publicId).orElseThrow(() -> new RuntimeException("Country with publicId " + publicId + " not found"));
    }

    public CountryDTO getCountryDTOByPublicId(String publicId) {
        return mapToCountryDTO(getCountryByPublicId(publicId));
    }

    @Transactional(readOnly = true)
    public Country getCountryByCode(String code) {
        return countryRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Country with code " + code + " not found"));
    }

    @Transactional
    public CountryDTO createCountry(CountryCreateUpdateDTO countryCreateDTO) {
        Country country = getCountryByCode(countryCreateDTO.getCode());
        if(country != null) {
            throw new RuntimeException("Country with code " + countryCreateDTO.getCode() + " already exists");
        }

        Currency currency = currencyService.getCurrencyByCode(countryCreateDTO.getCode());
        if(currency == null) {
            throw new RuntimeException("Currency with code " + countryCreateDTO.getCode() + " doesn't exists");
        }

        country = new Country();
        country.setPublicId(createPublicId());
        country.setCode(countryCreateDTO.getCode());
        country.setName(countryCreateDTO.getName());
        country.setLocale(countryCreateDTO.getLocale());
        country.setTimezone(countryCreateDTO.getTimezone());
        country.setFlagEmoji(countryCreateDTO.getFlagEmoji());
        country.setCurrency(currency);
        return mapToCountryDTO(countryRepository.save(country));
    }

    @Transactional
    public CountryDTO updateCountry(String publicId, CountryCreateUpdateDTO countryUpdateDTO) {
        
        Currency currency = currencyService.getCurrencyByCode(countryUpdateDTO.getCode());
        if(currency == null) {
            throw new RuntimeException("Currency with code " + countryUpdateDTO.getCode() + " doesn't exists");
        }

        Country country = getCountryByPublicId(publicId);
        country.setCode(countryUpdateDTO.getCode());
        country.setName(countryUpdateDTO.getName());
        country.setLocale(countryUpdateDTO.getLocale());
        country.setTimezone(countryUpdateDTO.getTimezone());
        country.setFlagEmoji(countryUpdateDTO.getFlagEmoji());
        country.setCurrency(currency);
        return mapToCountryDTO(countryRepository.save(country));
    }

    @Transactional
    public void deleteCountry(String publicId) {
        Country country = getCountryByPublicId(publicId);
        countryRepository.deleteById(country.getId());
    }
}
