package com.shopcardd.flashsale.repository;

import com.shopcardd.flashsale.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface DealRepository extends JpaRepository<Deal, String> {

    List<Deal> findByValidUntilAfterAndInventoryRemainingGreaterThan(
            Instant now,
            int inventory
    );
}
