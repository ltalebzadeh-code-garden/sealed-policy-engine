package com.sealedpolicy.engine.discount;

import java.math.BigDecimal;

/**
 * Fixed currency amount subtracted from the line total (never below zero).
 */
public record SeasonalPromotion(BigDecimal amountOff, String campaignCode) implements DiscountEvent {

    public SeasonalPromotion {
        if (amountOff == null || amountOff.signum() < 0) {
            throw new IllegalArgumentException("amountOff must be non-null and non-negative");
        }
    }
}
