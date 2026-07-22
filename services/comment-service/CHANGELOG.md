# Changelog

## Unreleased

### Added

- Anonymous-capable comment detail/root/reply reads with batch `reactionCount` and `likedByMe` enrichment.
- Root-only pagination, direct-reply pagination, grouped `replyCount`, and supporting PostgreSQL indexes.
- Post-owner comment moderation and transactional `CommentDeletedV1` cleanup events.
- Stable service-specific API error codes with correlation-aware 400/403/404/405/409/415/500/503 handling.
- Bounded Kafka retry and a Comment-specific DLT for `PostDeleted` consumer failures.
- A `local` Spring profile aligned with the root Compose Comment database port.
- Transactional Comment outbox relay for idempotent `PostCommentsDeletedV1` propagation.
- Transactional `CommentCreatedV1` and `CommentRepliedV1` notification events with resolved UUID
  recipients, self-action suppression and topic-aware outbox relay.
- Authenticated actor boundary, stable domain errors, reply validation, edit/delete/get and paginated discussion APIs.
- Internal comment availability contract and target validation port.
- Immutable comment `ownerId` in the internal availability contract for reaction notifications.
- PostgreSQL Testcontainers coverage for Flyway ownership, deleted-parent visibility and exact pagination.
- Correlation-aware create/delete command logs and Micrometer latency/error metrics with bounded tags.
- Single and batch active-comment count APIs for Feed/BFF composition, backed by one grouped query and a partial PostgreSQL index.
- Cross-service synchronous E2E coverage for the owned Comment availability contract.

### Changed

- Profile lookup and Interaction enrichment are non-critical decoration paths and degrade safely.
- Invalid Profile events now flow to the configured Kafka retry/DLT path instead of being acknowledged.
- Successful public API envelopes now consistently use common code `1000`.
- Default runtime database credentials are externalized; SQL logging is local-profile only.
- Poison Kafka records bypass retry and DLT publishing must receive broker acknowledgement.
- Comment commands no longer accept actor IDs from body/query.
- Deleted parents are returned as `[deleted]` placeholders only while replies exist.
- Post availability 403/404 and other 4xx responses are classified as non-retriable outcomes and excluded from circuit failure metrics.
- Namespaced Flyway resources under the Comment bounded context so composed test/runtime classpaths cannot discover another service's migrations.

### Fixed

- Declared the Post Feign path variable explicitly so the client can start without Java parameter-name metadata.
- Declared Comment controller path variables explicitly so endpoints do not depend on Java parameter-name metadata.
- Aligned the Docker image port with `server.port` and required the shared internal token in Compose.
- Added WireMock proof for outbound headers, status mapping, timeout, retry budget and circuit recovery.
