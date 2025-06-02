package com.njdealsandclicks.pricehistory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.pricehistory.PriceHistoryDTO;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/product/{productPublicId}")
    public List<PriceHistoryDTO> getPriceHistoriesByProductPublicId(@PathVariable("productPublicId") String productPublicId) {
        return priceHistoryService.getPriceHistoriesDTOsByProductPublicId(productPublicId);
    }

    @PostMapping("/create")
    public PriceHistory postMethodName(@RequestBody PriceHistory priceHistory) {        
        return priceHistoryService.createPriceHistory(priceHistory);
    }

    @DeleteMapping("/{id}")
    public void deletePriceHistory(@PathVariable UUID id) {
        priceHistoryService.deletePriceHistory(id);
    }

    @DeleteMapping("/product/{productPublicId}")
    public void deletePriceHistoriesByProductId(@PathVariable("productPublicId") String productPublicId) {
        priceHistoryService.deletePriceHistoriesByProductPublicId(productPublicId);
    }
    
    
}
