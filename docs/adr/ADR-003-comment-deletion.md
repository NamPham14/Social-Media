# ADR-003: Comment deletion with replies

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

Deletion is idempotent and uses soft delete. A deleted parent that still has replies remains queryable as a placeholder with content `[deleted]`; deleted leaf comments are omitted from post listings.

## Consequences

- Replies keep a stable parent reference.
- Deleted content is never exposed.
- Repeated delete requests succeed without changing state again.
