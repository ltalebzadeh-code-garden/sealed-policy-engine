# SealedPolicy Engine

**SealedPolicy Engine** is a demonstration of Modern Java 21 Data-Oriented Programming, replacing traditional conditional spaghetti with exhaustive pattern matching and sealed type hierarchies.

This repository is a small Java **21** sample: business rules use **sealed types** (a fixed set of variants), **records** (immutable data), and **switch expressions with pattern matching** so the compiler can check that every case is handled.

It helps teams **avoid “if / else soup”** when the domain has several distinct shapes (discount rules, claim types, events). Instead of one object with nullable fields and string type codes, each variant is its own type; behavior lives in **one exhaustive switch** per operation (calculate discount, calculate payout), which stays easier to read and safer to extend.

---

## What’s inside

| Area | Purpose |
|------|--------|
| **Discount** | Sealed `DiscountEvent` variants and a `DiscountEngine` that applies discounts via pattern matching. |
| **Claims** | Sealed `Claim` (`AutoClaim`, `HomeClaim`, `HealthClaim`) and a `ClaimProcessor` that computes payouts per variant. |
| **Persistence** | JDBC mapping from relational rows to those sealed claim types (used by the integration test). |

---

## Who it’s for

- Developers learning **modern Java** (sealed interfaces, records, switches).
- Anyone comparing **data-oriented** modeling to large conditional chains.
- Teams evaluating **exhaustive matching** as a way to catch missing cases at compile time.

---

## Build and test

```bash
mvn verify
```

Unit tests run in any normal JDK 21 environment.

---

## Integration test (Testcontainers)

The portfolio scenario **`PolicyPortfolioScenarioIT`** starts **PostgreSQL in Docker** (Testcontainers), loads claims through JDBC, and checks payout totals against expected values.

**It does not run by default** when Docker is missing or unusable: Testcontainers detects no Docker environment and **skips** that test so `mvn test` / `mvn verify` still succeed.

To **actually run** the integration test, set up your machine (or CI agent) so that:

- **Docker** (or a compatible engine with a Docker API) is **installed and running**.
- Your user (or the CI job) has **permission to use the Docker socket** (e.g. membership in the `docker` group on Linux, or Docker Desktop running on macOS/Windows).
- The environment can **pull** the Testcontainers base image (network access to the registry if required).

After that, the IT runs automatically with the rest of the suite when Docker is available; otherwise it remains skipped.

---

## Project layout

- `src/main/java/.../discount` — `DiscountEvent` hierarchy and `DiscountEngine`
- `src/main/java/.../claim` — `Claim` hierarchy
- `src/main/java/.../service` — `ClaimProcessor`
- `src/main/java/.../persistence` — `JdbcClaimRepository`
- `src/test/java/.../discount` — unit tests (JUnit 5, AssertJ)
- `src/test/java/.../persistence` — Testcontainers integration test
