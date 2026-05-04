CREATE TABLE IF NOT EXISTS policy_claims (
    id                      BIGSERIAL PRIMARY KEY,
    claim_kind              VARCHAR(20)  NOT NULL,
    policy_id               VARCHAR(64)  NOT NULL,
    status                  VARCHAR(16)  NOT NULL DEFAULT 'OPEN',
    auto_repair             NUMERIC(14, 2),
    auto_fault_pct          INT,
    auto_coverage           NUMERIC(14, 2),
    home_damage             NUMERIC(14, 2),
    home_dwelling           NUMERIC(14, 2),
    home_hurricane          BOOLEAN,
    home_hurricane_deductible_pct INT,
    health_allowed          NUMERIC(14, 2),
    health_annual_ded_rem   NUMERIC(14, 2),
    health_deductible       NUMERIC(14, 2),
    health_coinsurance_pct  INT
);
