package com.shopcardd.flashsale.service;

import com.shopcardd.flashsale.dto.CreateDealRequest;
import com.shopcardd.flashsale.dto.DealResponse;
import com.shopcardd.flashsale.exception.*;
import com.shopcardd.flashsale.model.Claim;
import com.shopcardd.flashsale.model.Deal;
import com.shopcardd.flashsale.repository.ClaimRepository;
import com.shopcardd.flashsale.repository.DealRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ClaimRepository claimRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /* -------------------- CREATE DEAL -------------------- */

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

    private DealResponse toResponse(Deal deal) {
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

    /* -------------------- CLAIM DEAL (CORE CHALLENGE) -------------------- */

    @Transactional
    public void claimDeal(String dealId, String userId) {

        String lockKey = "lock:deal:" + dealId;

        Boolean lockAcquired = redisTemplate
                .opsForValue()
                .setIfAbsent(lockKey, userId, Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new DealLockedException("Deal is currently being claimed. Try again.");
        }

        try {
            Deal deal = dealRepository.findById(dealId)
                    .orElseThrow(() -> new DealNotFoundException("Deal not found"));

            if (deal.getValidUntil().isBefore(Instant.now())) {
                throw new DealExpiredException("Deal expired");
            }

            if (deal.getInventoryRemaining() <= 0) {
                throw new DealSoldOutException("Deal sold out");
            }

            if (claimRepository.findByDealIdAndUserId(dealId, userId).isPresent()) {
                throw new AlreadyClaimedException("User already claimed this deal");
            }

            deal.setInventoryRemaining(deal.getInventoryRemaining() - 1);
            dealRepository.save(deal);

            claimRepository.save(
                    Claim.builder()
                            .dealId(dealId)
                            .userId(userId)
                            .claimedAt(Instant.now())
                            .build()
            );

        } finally {
            String owner = redisTemplate.opsForValue().get(lockKey);
            if (userId.equals(owner)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    /* -------------------- DISCOVER DEALS (WITH REDIS CACHE) -------------------- */

    public List<DealResponse> discoverDeals(double lat, double lng, double radiusKm) {

        String cacheKey = String.format(
                "cache:deals:%.4f:%.4f:%.1f",
                lat, lng, radiusKm
        );

        // 1️⃣ Try Redis cache
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(
                        cached,
                        new TypeReference<List<DealResponse>>() {}
                );
            } catch (Exception ignored) {
                // fallback to DB
            }
        }

        // 2️⃣ Fetch active deals from DB
        List<Deal> activeDeals =
                dealRepository.findByValidUntilAfterAndInventoryRemainingGreaterThan(
                        Instant.now(), 0
                );

        List<DealResponse> response = activeDeals.stream()
                .filter(deal ->
                        GeoUtils.distanceKm(
                                lat, lng,
                                deal.getLatitude(), deal.getLongitude()
                        ) <= radiusKm
                )
                .map(deal -> {
                    double distance = GeoUtils.distanceKm(
                            lat, lng,
                            deal.getLatitude(), deal.getLongitude()
                    );

                    return DealResponse.builder()
                            .dealId(deal.getDealId())
                            .merchantId(deal.getMerchantId())
                            .title(deal.getTitle())
                            .totalVouchers(deal.getTotalInventory())
                            .inventoryRemaining(deal.getInventoryRemaining())
                            .validUntil(deal.getValidUntil())
                            .distanceKm(distance)
                            .location(
                                    DealResponse.Location.builder()
                                            .lat(deal.getLatitude())
                                            .lng(deal.getLongitude())
                                            .build()
                            )
                            .build();
                })

                .toList();

        // 3️⃣ Store in Redis (TTL = 30s)
        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(response),
                    Duration.ofSeconds(30)
            );
        } catch (Exception ignored) {}

        return response;
    }
}
