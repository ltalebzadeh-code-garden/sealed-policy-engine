package com.sealedpolicy.engine.claim;

/**
 * Root of the claim hierarchy; exhaustive switches over this type are checked by the compiler.
 */
public sealed interface Claim permits AutoClaim, HomeClaim, HealthClaim {

}
