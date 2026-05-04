package com.sealedpolicy.engine.claim;

import java.math.BigDecimal;

/**
 * Medical claim with deductible and coinsurance after deductible is satisfied.
 */
public record HealthClaim(
        String policyId,
        BigDecimal allowedCharges,
        BigDecimal annualDeductibleRemaining,
        BigDecimal deductible,
        int coinsurancePercent
) implements Claim {

    public HealthClaim {
        if (allowedCharges == null || allowedCharges.signum() < 0) {
            throw new IllegalArgumentException("allowedCharges invalid");
        }
        if (annualDeductibleRemaining == null || annualDeductibleRemaining.signum() < 0) {
            throw new IllegalArgumentException("annualDeductibleRemaining invalid");
        }
        if (deductible == null || deductible.signum() < 0) {
            throw new IllegalArgumentException("deductible invalid");
        }
        if (coinsurancePercent < 0 || coinsurancePercent > 100) {
            throw new IllegalArgumentException("coinsurancePercent must be 0..100");
        }
    }
}
