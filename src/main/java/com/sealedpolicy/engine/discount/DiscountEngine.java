package com.sealedpolicy.engine.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Applies {@link DiscountEvent} variants to a base price using exhaustive pattern matching.
 */
public final class DiscountEngine {

    private static final int MONEY_SCALE = 2;

    /**
     * Returns the monetary amount of discount (always &gt;= 0), never greater than {@code basePrice}.
     */
    public BigDecimal discountAmount(DiscountEvent event, BigDecimal basePrice) {
        if (basePrice == null || basePrice.signum() < 0) {
            throw new IllegalArgumentException("basePrice must be non-null and non-negative");
        }
        BigDecimal raw = switch (event) {
            case LoyaltyReward lr -> basePrice.multiply(lr.rate()).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            case SeasonalPromotion sp -> sp.amountOff().min(basePrice);
            case VolumePurchase vp when vp.quantity() >= vp.threshold() ->
                    vp.rebatePerUnit().multiply(BigDecimal.valueOf(vp.quantity())).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            case VolumePurchase ignored -> BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            case ClearanceMarkdown cm ->
                    basePrice.multiply(BigDecimal.valueOf(cm.percentOff()))
                            .divide(BigDecimal.valueOf(100), MONEY_SCALE + 2, RoundingMode.HALF_UP)
                            .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        };
        return raw.min(basePrice).max(BigDecimal.ZERO);
    }

    public BigDecimal priceAfterDiscount(DiscountEvent event, BigDecimal basePrice) {
        return basePrice.subtract(discountAmount(event, basePrice)).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
