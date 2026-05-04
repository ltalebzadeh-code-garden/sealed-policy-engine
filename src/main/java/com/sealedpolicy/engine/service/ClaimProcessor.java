package com.sealedpolicy.engine.service;

import com.sealedpolicy.engine.claim.AutoClaim;
import com.sealedpolicy.engine.claim.Claim;
import com.sealedpolicy.engine.claim.HealthClaim;
import com.sealedpolicy.engine.claim.HomeClaim;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates indemnity-style payouts using exhaustive pattern matching on {@link Claim}.
 */
public final class ClaimProcessor {

    private static final int SCALE = 2;

    public BigDecimal calculatePayout(Claim claim) {
        return switch (claim) {
            case AutoClaim ac -> payoutAuto(ac);
            case HomeClaim hc -> payoutHome(hc);
            case HealthClaim h -> payoutHealth(h);
        };
    }

    private static BigDecimal payoutAuto(AutoClaim ac) {
        BigDecimal faultFactor = BigDecimal.valueOf(100 - ac.insuredFaultPercent())
                .divide(BigDecimal.valueOf(100), SCALE + 4, RoundingMode.HALF_UP);
        BigDecimal gross = ac.repairEstimate().multiply(faultFactor).setScale(SCALE, RoundingMode.HALF_UP);
        return gross.min(ac.coverageLimit());
    }

    private static BigDecimal payoutHome(HomeClaim hc) {
        BigDecimal covered = hc.damageEstimate().min(hc.dwellingCoverage());
        if (hc.hurricaneEvent() && hc.hurricaneDeductiblePercent() > 0) {
            BigDecimal ded = covered.multiply(BigDecimal.valueOf(hc.hurricaneDeductiblePercent()))
                    .divide(BigDecimal.valueOf(100), SCALE + 4, RoundingMode.HALF_UP)
                    .setScale(SCALE, RoundingMode.HALF_UP);
            covered = covered.subtract(ded).max(BigDecimal.ZERO);
        }
        return covered.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Applies plan deductible (capped by what remains for the policy year), then coinsurance on the remainder.
     */
    private static BigDecimal payoutHealth(HealthClaim h) {
        BigDecimal allowed = h.allowedCharges();
        BigDecimal appliedDeductible = allowed.min(h.deductible()).min(h.annualDeductibleRemaining());
        BigDecimal afterDeductible = allowed.subtract(appliedDeductible).max(BigDecimal.ZERO);
        BigDecimal planShare = BigDecimal.valueOf(100 - h.coinsurancePercent())
                .divide(BigDecimal.valueOf(100), SCALE + 4, RoundingMode.HALF_UP);
        return afterDeductible.multiply(planShare).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
