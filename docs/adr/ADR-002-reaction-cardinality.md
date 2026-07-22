# ADR-002: Reaction cardinality

- Status: ACCEPTED
- Date: 2026-07-22

## Decision

A user may have at most one reaction on a target. The initial product supports `LIKE` only, and the
tuple `(actor, target type, target id)` is unique. Repeating LIKE is idempotent.

## Consequences

- `CLAP` is removed from the public contract and existing CLAP rows are removed by migration V4.
- The database unique constraint deliberately excludes `reaction_type` so future `LOVE`, `HAHA`, etc.
  remain mutually exclusive for one actor and target.
- A future multi-reaction release should expose `myReaction` and change the existing row atomically;
  it must not restore uniqueness per reaction type.
