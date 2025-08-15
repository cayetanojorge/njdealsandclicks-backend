package com.njdealsandclicks.searchrequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.njdealsandclicks.dto.searchrequest.SearchRequestCreateDTO;


@RestController
@RequestMapping("/api/public/search-request")
public class SearchRequestPublicController {

    private final SearchRequestService searchRequestService;

    public SearchRequestPublicController(SearchRequestService searchRequestService) {
        this.searchRequestService = searchRequestService;
    }

    @PostMapping(value = "/save-request", consumes = "application/json")
    public ResponseEntity<Void> saveSearchRequest(@Valid @RequestBody SearchRequestCreateDTO body, HttpServletRequest req) {
        searchRequestService.saveOrBump(body, req);
        return ResponseEntity.accepted().build(); // 202, no body
    }
}
