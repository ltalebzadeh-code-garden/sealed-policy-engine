package com.sealedpolicy.engine.persistence;

import com.sealedpolicy.engine.claim.AutoClaim;
import com.sealedpolicy.engine.claim.Claim;
import com.sealedpolicy.engine.claim.HealthClaim;
import com.sealedpolicy.engine.claim.HomeClaim;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@link Claim} rows from a relational store for integration-style scenarios.
 */
public final class JdbcClaimRepository {

    public List<Claim> loadOpenClaims(Connection connection) throws SQLException {
        String sql = """
                SELECT claim_kind, policy_id,
                       auto_repair, auto_fault_pct, auto_coverage,
                       home_damage, home_dwelling, home_hurricane, home_hurricane_deductible_pct,
                       health_allowed, health_annual_ded_rem, health_deductible, health_coinsurance_pct
                FROM policy_claims
                WHERE status = 'OPEN'
                ORDER BY id
                """;
        try (var ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<Claim> out = new ArrayList<>();
            while (rs.next()) {
                out.add(mapRow(rs));
            }
            return List.copyOf(out);
        }
    }

    public void insertClaim(
            Connection connection,
            String kind,
            String policyId,
            BigDecimal autoRepair,
            Integer autoFaultPct,
            BigDecimal autoCoverage,
            BigDecimal homeDamage,
            BigDecimal homeDwelling,
            Boolean homeHurricane,
            Integer homeHurricaneDedPct,
            BigDecimal healthAllowed,
            BigDecimal healthAnnualDedRem,
            BigDecimal healthDeductible,
            Integer healthCoinsurancePct
    ) throws SQLException {
        String sql = """
                INSERT INTO policy_claims (
                    claim_kind, policy_id, status,
                    auto_repair, auto_fault_pct, auto_coverage,
                    home_damage, home_dwelling, home_hurricane, home_hurricane_deductible_pct,
                    health_allowed, health_annual_ded_rem, health_deductible, health_coinsurance_pct
                ) VALUES (?,?, 'OPEN',?,?,?,?,?,?,?,?,?,?,?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, kind);
            ps.setString(i++, policyId);
            ps.setObject(i++, autoRepair);
            ps.setObject(i++, autoFaultPct);
            ps.setObject(i++, autoCoverage);
            ps.setObject(i++, homeDamage);
            ps.setObject(i++, homeDwelling);
            ps.setObject(i++, homeHurricane);
            ps.setObject(i++, homeHurricaneDedPct);
            ps.setObject(i++, healthAllowed);
            ps.setObject(i++, healthAnnualDedRem);
            ps.setObject(i++, healthDeductible);
            ps.setObject(i++, healthCoinsurancePct);
            ps.executeUpdate();
        }
    }

    private static Claim mapRow(ResultSet rs) throws SQLException {
        String kind = rs.getString("claim_kind");
        String policyId = rs.getString("policy_id");
        return switch (kind) {
            case "AUTO" -> new AutoClaim(
                    policyId,
                    rs.getBigDecimal("auto_repair"),
                    rs.getInt("auto_fault_pct"),
                    rs.getBigDecimal("auto_coverage")
            );
            case "HOME" -> new HomeClaim(
                    policyId,
                    rs.getBigDecimal("home_damage"),
                    rs.getBigDecimal("home_dwelling"),
                    rs.getBoolean("home_hurricane"),
                    rs.getInt("home_hurricane_deductible_pct")
            );
            case "HEALTH" -> new HealthClaim(
                    policyId,
                    rs.getBigDecimal("health_allowed"),
                    rs.getBigDecimal("health_annual_ded_rem"),
                    rs.getBigDecimal("health_deductible"),
                    rs.getInt("health_coinsurance_pct")
            );
            default -> throw new IllegalStateException("Unknown claim_kind: " + kind);
        };
    }
}
