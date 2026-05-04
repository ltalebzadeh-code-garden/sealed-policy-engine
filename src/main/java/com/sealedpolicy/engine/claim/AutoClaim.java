package com.sealedpolicy.engine.claim;

import java.math.BigDecimal;

/**
 * Collision / liability style auto claim. {@code insuredFaultPercent} is the policyholder share of fault (0–100).
 */
public record AutoClaim(
        String policyId,
        BigDecimal repairEstimate,
        int insuredFaultPercent,
        BigDecimal coverageLimit
) implements Claim {

    public AutoClaim {
        if (insuredFaultPercent < 0 || insuredFaultPercent > 100) {
            throw new IllegalArgumentException("insuredFaultPercent must be 0..100");
        }
        if (repairEstimate == null || repairEstimate.signum() < 0) {
            throw new IllegalArgumentException("repairEstimate invalid");
        }
        if (coverageLimit == null || coverageLimit.signum() < 0) {
            throw new IllegalArgumentException("coverageLimit invalid");
        }
    }
}
