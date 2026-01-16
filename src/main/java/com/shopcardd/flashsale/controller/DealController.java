package com.shopcardd.flashsale.controller;

import com.shopcardd.flashsale.dto.ClaimRequest;
import com.shopcardd.flashsale.dto.CreateDealRequest;
import com.shopcardd.flashsale.dto.DealResponse;
import com.shopcardd.flashsale.model.Deal;
import com.shopcardd.flashsale.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DealResponse createDeal(
            @Valid @RequestBody CreateDealRequest request
    ) {
        return dealService.createDeal(request);
    }


    @GetMapping("/discover")
    public List<DealResponse> discoverDeals(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius
    ) {
        return dealService.discoverDeals(lat, lng, radius)
                .stream()
                .map(dealService::toResponse)
                .toList();
    }

    @PostMapping("/{dealId}/claim")
    public Map<String, String> claimDeal(
            @PathVariable String dealId,
            @Valid @RequestBody ClaimRequest request
    ) {
        dealService.claimDeal(dealId, request.getUserId());
        return Map.of(
                "status", "SUCCESS",
                "message", "Voucher claimed"
        );
    }



}
