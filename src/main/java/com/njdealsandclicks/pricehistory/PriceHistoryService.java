package com.njdealsandclicks.pricehistory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.njdealsandclicks.dto.pricehistory.PriceHistoryDTO;


@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    
    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    private PriceHistoryDTO mapToPriceHistoryDTO(PriceHistory priceHistory) {
        PriceHistoryDTO priceHistoryDTO = new PriceHistoryDTO();
        priceHistoryDTO.setPrice(priceHistory.getPrice());
        priceHistoryDTO.setRecordedAt(priceHistory.getRecordedAt());
        return priceHistoryDTO;
    }
    
    @Transactional(readOnly = true)
    public List<PriceHistory> getPriceHistoriesByProductMarketId(UUID productMarketId) {
        return priceHistoryRepository.findByProductId(productMarketId);
    }

    @Transactional(readOnly = true)
    public List<PriceHistory> getPriceHistoriesByProductMarketPublicId(String productMarketPublicId) {
        return priceHistoryRepository.findByProductPublicId(productMarketPublicId);
    }

    public List<PriceHistoryDTO> getPriceHistoriesDTOsByProductMarketPublicId(String productMarketPublicId) {
        List<PriceHistory> priceHistories = getPriceHistoriesByProductMarketPublicId(productMarketPublicId);
        return priceHistories.stream()
            .map(this::mapToPriceHistoryDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public PriceHistory createPriceHistory(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    @Transactional
    public void deletePriceHistory(UUID id) {
        priceHistoryRepository.deleteById(id);
    }
    
    @Transactional
    public void deletePriceHistoriesByProductPublicId(String productPublicId) {
        priceHistoryRepository.deleteByProductPublicId(productPublicId);
    }
}
