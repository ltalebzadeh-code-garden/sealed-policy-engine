package com.sealedpolicy.engine.persistence;

import com.sealedpolicy.engine.claim.Claim;
import com.sealedpolicy.engine.service.ClaimProcessor;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: claims persisted in PostgreSQL (Testcontainers), mapped to sealed
 * {@link Claim} types, payouts computed with the same production {@link ClaimProcessor} logic.
 */
@Testcontainers(disabledWithoutDocker = false)
class PolicyPortfolioScenarioIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    /**
     * Real-world composite scenario: multi-line family policies after a named storm and a hospital stay,
     * cross-checked against hand-calculated actuarial totals.
     */
    @Test
    void familyPortfolioOpenClaimsMatchExpectedTotalPayout() throws Exception {
        applySchema();
        seedPortfolioScenario();

        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword())) {

            JdbcClaimRepository repository = new JdbcClaimRepository();
            ClaimProcessor processor = new ClaimProcessor();

            BigDecimal total = repository.loadOpenClaims(connection).stream()
                    .map(processor::calculatePayout)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Auto: 18500 repair, 20% fault -> 14800; Home: 90000 damage, 2% hurricane ded on loss -> 88200;
            // Health: 24000 allowed, 3000 ded (annual remaining sufficient), 20% member coinsurance -> 16800 plan.
            assertThat(total).isEqualByComparingTo("119800.00");
        }
    }

    private static void applySchema() throws Exception {
        final String ddl;
        try (InputStream in = PolicyPortfolioScenarioIT.class.getResourceAsStream("/db/policy_claims.sql")) {
            if (in == null) {
                throw new IllegalStateException("Missing classpath resource /db/policy_claims.sql");
            }
            ddl = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        try (Connection c = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             Statement st = c.createStatement()) {
            st.execute(ddl);
        }
    }

    private static void seedPortfolioScenario() throws Exception {
        try (Connection c = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {

            JdbcClaimRepository repo = new JdbcClaimRepository();

            repo.insertClaim(c, "AUTO", "AUTO-77821",
                    new BigDecimal("18500.00"), 20, new BigDecimal("25000.00"),
                    null, null, null, null,
                    null, null, null, null);

            repo.insertClaim(c, "HOME", "HOME-99104",
                    null, null, null,
                    new BigDecimal("90000.00"), new BigDecimal("750000.00"), true, 2,
                    null, null, null, null);

            repo.insertClaim(c, "HEALTH", "HLT-44102",
                    null, null, null,
                    null, null, null, null,
                    new BigDecimal("24000.00"), new BigDecimal("5000.00"), new BigDecimal("3000.00"), 20);
        }
    }
}
