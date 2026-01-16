package com.shopcardd.flashsale.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank
    private String userId;
}
