package com.shopcardd.flashsale.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class CreateDealRequest {

    @NotBlank
    @JsonProperty("merchant_id")
    private String merchantId;

    @NotBlank
    private String title;

    @Min(1)
    @JsonProperty("total_vouchers")
    private int totalVouchers;

    @NotNull
    @JsonProperty("valid_until")
    private Instant validUntil;

    @NotNull
    private Location location;

    @Data
    public static class Location {

        private double lat;

        @JsonProperty("long")
        private double lng;
    }
}
