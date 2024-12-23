package com.njdealsandclicks.pricehistory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {
    
    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/product/{productId}")
    public List<PriceHistory> getPriceHistoriesByProductId(@PathVariable Long productId) {
        return priceHistoryService.getPriceHistoriesByProductId(productId);
    }

    @PostMapping("/create")
    public PriceHistory postMethodName(@RequestBody PriceHistory priceHistory) {        
        return priceHistoryService.createPriceHistory(priceHistory);
    }

    @DeleteMapping("/{id}")
    public void deletePriceHistory(@PathVariable Long id) {
        priceHistoryService.deletePriceHistory(id);
    }

    @DeleteMapping("/product/{productId}")
    public void deletePriceHistoriesByProductId(@PathVariable Long productId) {
        priceHistoryService.deletePriceHistoriesByProductId(productId);
    }
    
    
}
