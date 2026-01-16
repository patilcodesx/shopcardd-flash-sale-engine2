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
public Map<String, List<DealResponse>> discoverDeals(
        @RequestParam double lat,
        @RequestParam double lng,
        @RequestParam double radius
) {
    List<DealResponse> deals = dealService.discoverDeals(lat, lng, radius);

    return Map.of("deals", deals);
}


 @PostMapping("/{dealId}/claim")
public Map<String, String> claimDeal(
        @PathVariable String dealId,
        @RequestParam(required = false) String userId,
        @RequestBody(required = false) ClaimRequest request
) {
    String finalUserId;

    if (userId != null && !userId.isBlank()) {
        finalUserId = userId;
    } else if (request != null && request.getUserId() != null) {
        finalUserId = request.getUserId();
    } else {
        throw new IllegalArgumentException("userId is required");
    }

    dealService.claimDeal(dealId, finalUserId);

   return Map.of(
        "status", "Success",
        "voucher_code", "SHOP-" + dealId.substring(0, 6)
    );
}




}
