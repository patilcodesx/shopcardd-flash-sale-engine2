package com.shopcardd.flashsale.repository;

import com.shopcardd.flashsale.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByDealIdAndUserId(String dealId, String userId);
}
