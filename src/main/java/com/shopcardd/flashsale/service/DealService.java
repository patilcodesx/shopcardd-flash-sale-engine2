package com.shopcardd.flashsale.service;
import com.shopcardd.flashsale.exception.DealNotFoundException;
import com.shopcardd.flashsale.dto.CreateDealRequest;
import com.shopcardd.flashsale.dto.DealResponse;
import com.shopcardd.flashsale.model.Deal;
import com.shopcardd.flashsale.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import com.shopcardd.flashsale.model.Claim;
import com.shopcardd.flashsale.repository.ClaimRepository;
import org.springframework.transaction.annotation.Transactional;
import com.shopcardd.flashsale.exception.AlreadyClaimedException;
import com.shopcardd.flashsale.exception.DealSoldOutException;
import com.shopcardd.flashsale.exception.DealExpiredException;


@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ClaimRepository claimRepository;

    public DealResponse createDeal(CreateDealRequest request) {

        Deal deal = Deal.builder()
                .merchantId(request.getMerchantId())
                .title(request.getTitle())
                .totalInventory(request.getTotalVouchers())
                .inventoryRemaining(request.getTotalVouchers())
                .validUntil(request.getValidUntil())
                .latitude(request.getLocation().getLat())
                .longitude(request.getLocation().getLng())
                .build();


        return toResponse(dealRepository.save(deal));
    }


    public DealResponse toResponse(Deal deal) {
        return DealResponse.builder()
                .dealId(deal.getDealId())
                .merchantId(deal.getMerchantId())
                .title(deal.getTitle())
                .totalVouchers(deal.getTotalInventory())
                .inventoryRemaining(deal.getInventoryRemaining())
                .validUntil(deal.getValidUntil())
                .location(
                        DealResponse.Location.builder()
                                .lat(deal.getLatitude())
                                .lng(deal.getLongitude())
                                .build()
                )
                .build();
    }

    @Transactional
    public void claimDeal(String dealId, String userId) {



        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealNotFoundException("Deal not found"));


        if (deal.getValidUntil().isBefore(Instant.now())) {
            throw new DealExpiredException("Deal expired");
        }

        if (deal.getInventoryRemaining() <= 0) {
            throw new DealSoldOutException("Deal sold out");
        }


        boolean alreadyClaimed =
                claimRepository.findByDealIdAndUserId(dealId, userId).isPresent();



        if (alreadyClaimed) {
            throw new AlreadyClaimedException("User already claimed this deal");
        }


        // reduce inventory
        deal.setInventoryRemaining(deal.getInventoryRemaining() - 1);
        dealRepository.save(deal);

        // save claim
        Claim claim = Claim.builder()
                .dealId(dealId)
                .userId(userId)
                .claimedAt(Instant.now())
                .build();

        claimRepository.save(claim);
    }

    public List<Deal> discoverDeals(double lat, double lng, double radiusKm) {

        List<Deal> activeDeals =
                dealRepository.findByValidUntilAfterAndInventoryRemainingGreaterThan(
                        Instant.now(), 0
                );

        return activeDeals.stream()
                .filter(deal -> GeoUtils.distanceKm(
                        lat, lng,
                        deal.getLatitude(), deal.getLongitude()
                ) <= radiusKm)
                .collect(Collectors.toList());
    }
}
