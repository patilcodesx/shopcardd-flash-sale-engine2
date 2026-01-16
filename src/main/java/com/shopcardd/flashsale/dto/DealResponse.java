package com.shopcardd.flashsale.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class DealResponse {

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("merchant_id")
    private String merchantId;

    private String title;

    @JsonProperty("total_vouchers")
    private int totalVouchers;

    @JsonProperty("inventory_remaining")
    private int inventoryRemaining;

    @JsonProperty("valid_until")
    private Instant validUntil;

    private Location location;

    @Data
    @Builder
    public static class Location {
        private double lat;

        @JsonProperty("long")
        private double lng;
    }
}
