package com.shopcardd.flashsale.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "claims",
        uniqueConstraints = @UniqueConstraint(columnNames = {"deal_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deal_id", nullable = false)
    private String dealId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private Instant claimedAt;
}
