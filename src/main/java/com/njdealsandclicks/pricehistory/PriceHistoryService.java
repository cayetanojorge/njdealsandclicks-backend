package com.njdealsandclicks.pricehistory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }
    
    @Transactional(readOnly = true)
    public List<PriceHistory> getPriceHistoriesByProductId(Long productId) {
        return priceHistoryRepository.findByProductId(productId);
    }
    
    @Transactional
    public PriceHistory createPriceHistory(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    @Transactional
    public void deletePriceHistory(Long id) {
        priceHistoryRepository.deleteById(id);
    }
    
    @Transactional
    public void deletePriceHistoriesByProductId(Long productId) {
        priceHistoryRepository.findByProductId(productId).clear();
    }
}
