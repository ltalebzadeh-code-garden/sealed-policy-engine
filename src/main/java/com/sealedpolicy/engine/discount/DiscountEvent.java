package com.sealedpolicy.engine.discount;

/**
 * Domain events that influence how a price is discounted. Sealed so every variant is known at compile time.
 */
public sealed interface DiscountEvent
        permits LoyaltyReward, SeasonalPromotion, VolumePurchase, ClearanceMarkdown {

}
