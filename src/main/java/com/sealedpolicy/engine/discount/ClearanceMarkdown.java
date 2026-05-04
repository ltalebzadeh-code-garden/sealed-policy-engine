package com.sealedpolicy.engine.discount;

/**
 * Percentage markdown on discontinued inventory (0–100 percent).
 */
public record ClearanceMarkdown(int percentOff, String sku) implements DiscountEvent {

    public ClearanceMarkdown {
        if (percentOff < 0 || percentOff > 100) {
            throw new IllegalArgumentException("percentOff must be between 0 and 100");
        }
    }
}
