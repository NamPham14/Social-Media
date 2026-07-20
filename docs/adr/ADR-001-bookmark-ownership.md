# ADR-001: Bookmark ownership

- Status: ACCEPTED
- Date: 2026-07-16

## Decision

Post Service is the only owner of bookmarks. Interaction Service owns reactions only and therefore supports `LIKE` and `CLAP`, not `BOOKMARK`.

## Consequences

- Bookmark APIs and persistence remain in Post Service.
- `BOOKMARK` is removed from the Interaction API and counter model.
- Existing clients must use the Post bookmark contract.
