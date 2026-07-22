# ADR-006: Counter composition

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

Interaction Service exposes single-target and batch summary APIs. A summary combines `reactionCount`
and actor-aware `likedByMe`; without an actor, `likedByMe=false` and no actor-ledger query runs. The batch
form accepts up to 100 targets and uses a constant number of database queries rather than one query/request
per target.

Comment Service remains the owner of `commentCount`. Comment read responses are enriched synchronously by
one Interaction summary batch per page and degrade to zero/false if enrichment is unavailable. Feed/BFF
consumers compose one Comment count batch request and one Interaction summary batch request for each page
of posts. Embedding those fields directly in Post requires a denormalized projection and a separate ADR.
