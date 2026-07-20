# ADR-002: Reaction cardinality

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

A user may apply `LIKE` and `CLAP` to the same target at the same time. A tuple `(actor, target type, target id, reaction type)` is unique. Repeating the same reaction is idempotent.

## Consequences

- Changing a reaction is not a use case; clients add/remove each type independently.
- The database unique constraint includes `reaction_type`.
- This models CLAP as a boolean reaction, not Medium's multi-clap quantity.
