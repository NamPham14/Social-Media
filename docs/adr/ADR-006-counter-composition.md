# ADR-006: Counter composition

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

Interaction Service exposes single-target and batch counter APIs. Feed consumers compose these batch APIs first; a denormalized feed projection requires separate evidence and an ADR.
