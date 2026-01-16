package com.shopcardd.flashsale.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "deals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String dealId;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int totalInventory;

    @Column(nullable = false)
    private int inventoryRemaining;

    @Column(nullable = false)
    private Instant validUntil;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;
}
