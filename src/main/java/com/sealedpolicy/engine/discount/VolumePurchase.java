package com.sealedpolicy.engine.discount;

import java.math.BigDecimal;

/**
 * Per-unit rebate when quantity meets threshold.
 */
public record VolumePurchase(int quantity, int threshold, BigDecimal rebatePerUnit) implements DiscountEvent {

    public VolumePurchase {
        if (quantity < 0 || threshold < 1) {
            throw new IllegalArgumentException("invalid quantity or threshold");
        }
        if (rebatePerUnit == null || rebatePerUnit.signum() < 0) {
            throw new IllegalArgumentException("rebatePerUnit must be non-null and non-negative");
        }
    }
}
