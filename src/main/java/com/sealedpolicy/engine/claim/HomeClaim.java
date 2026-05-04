package com.sealedpolicy.engine.claim;

import java.math.BigDecimal;

/**
 * Property damage claim with dwelling coverage cap and optional hurricane deductible (percent of loss).
 */
public record HomeClaim(
        String policyId,
        BigDecimal damageEstimate,
        BigDecimal dwellingCoverage,
        boolean hurricaneEvent,
        int hurricaneDeductiblePercent
) implements Claim {

    public HomeClaim {
        if (damageEstimate == null || damageEstimate.signum() < 0) {
            throw new IllegalArgumentException("damageEstimate invalid");
        }
        if (dwellingCoverage == null || dwellingCoverage.signum() < 0) {
            throw new IllegalArgumentException("dwellingCoverage invalid");
        }
        if (hurricaneDeductiblePercent < 0 || hurricaneDeductiblePercent > 100) {
            throw new IllegalArgumentException("hurricaneDeductiblePercent must be 0..100");
        }
    }
}
