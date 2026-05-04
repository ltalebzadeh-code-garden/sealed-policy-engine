package com.sealedpolicy.engine.discount;

import java.math.BigDecimal;

/**
 * Percentage reduction granted for loyalty tier (basis points: 100 = 1%).
 */
public record LoyaltyReward(int basisPoints, String tierName) implements DiscountEvent {

    public LoyaltyReward {
        if (basisPoints < 0 || basisPoints > 10_000) {
            throw new IllegalArgumentException("basisPoints must be between 0 and 10000");
        }
    }

    public BigDecimal rate() {
        return BigDecimal.valueOf(basisPoints, 4);
    }
}
