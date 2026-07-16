# ADR-005: Target validation

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

Commands validate targets synchronously through application outbound ports implemented by OpenFeign adapters. Local projections are deferred until feed/read-model requirements justify eventual consistency.

## Failure semantics

- Missing target: 404.
- Existing but unavailable for the action: 409.
- Timeout/provider failure: 503 (fail closed).
- Local read queries remain available when a provider is down.
