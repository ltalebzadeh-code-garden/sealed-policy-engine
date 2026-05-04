package com.sealedpolicy.engine.discount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountEngineTest {

    private DiscountEngine engine;

    @BeforeEach
    void setUp() {
        engine = new DiscountEngine();
    }

    @Test
    void loyaltyRewardAppliesBasisPointsToBasePrice() {
        var event = new LoyaltyReward(750, "GOLD"); // 7.5%
        BigDecimal base = new BigDecimal("199.99");
        assertThat(engine.discountAmount(event, base)).isEqualByComparingTo("15.00");
        assertThat(engine.priceAfterDiscount(event, base)).isEqualByComparingTo("184.99");
    }

    @Test
    void seasonalPromotionIsCappedByBasePrice() {
        var event = new SeasonalPromotion(new BigDecimal("500.00"), "SPRING26");
        BigDecimal base = new BigDecimal("40.00");
        assertThat(engine.discountAmount(event, base)).isEqualByComparingTo("40.00");
        assertThat(engine.priceAfterDiscount(event, base)).isEqualByComparingTo("0.00");
    }

    @Test
    void volumePurchaseRequiresThreshold() {
        var below = new VolumePurchase(4, 10, new BigDecimal("2.50"));
        BigDecimal base = new BigDecimal("100.00");
        assertThat(engine.discountAmount(below, base)).isEqualByComparingTo("0.00");

        var met = new VolumePurchase(12, 10, new BigDecimal("1.00"));
        assertThat(engine.discountAmount(met, base)).isEqualByComparingTo("12.00");
    }

    @Test
    void clearanceMarkdownUsesPercentOff() {
        var event = new ClearanceMarkdown(25, "SKU-4412");
        BigDecimal base = new BigDecimal("80.00");
        assertThat(engine.discountAmount(event, base)).isEqualByComparingTo("20.00");
    }

    @Test
    void rejectsNegativeBasePrice() {
        assertThatThrownBy(() -> engine.discountAmount(new LoyaltyReward(100, "X"), new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
