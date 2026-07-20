# Comment/Interaction final architecture audit

## Verdict

The owned synchronous slice and deletion-cleanup event slice are complete enough for integration
with the rest of the project. The production-wide flow still depends on Post event reliability,
managed Kafka topic/alert provisioning and any future Notification contracts owned by other teams.

| Area | Status | Evidence or remaining condition |
| --- | --- | --- |
| Bounded contexts and decisions | Complete | ADR-001..007 are accepted; Bookmark remains in Post; LIKE and CLAP may coexist; replies are one level; parent deletion is soft delete. |
| Comment commands and queries | Complete | Create/reply/edit/idempotent delete/get/page/count/batch-count with actor ownership and PostgreSQL tests. |
| Interaction commands and queries | Complete | Idempotent LIKE/CLAP add/remove/current state/single and batch counters with atomic SQL, concurrency and rollback tests. |
| Synchronous target validation | Complete for current contracts | Application ports, Feign adapters, Eureka names, correlation/internal-token propagation and fail-closed behavior are tested. |
| Resilience and observability | Complete for synchronous calls | Explicit timeouts, bounded retry, per-dependency circuits, structured command logs, metrics and runbook. |
| Comment-to-Interaction integration | Complete | Real applications and isolated PostgreSQL databases are exercised over HTTP/Feign by the E2E module. |
| Post provider integration | Cross-team dependency | Consumers currently call public `GET /api/v1/posts/{id}` and can process `PostDeleted`; the internal availability contract and reliable production of that event remain owned by Post Service. |
| Deletion event reliability | Complete in owned services | Comment consumes `PostDeleted`, commits soft-delete plus outbox atomically, and relays `PostCommentsDeletedV1`; Interaction cleanup is idempotent with bounded retry and service-specific DLT recovery. |
| Notification events | Gated | CR-NOTIFICATION-001 is still proposed. No Notification event should be published before UUID schema, recipient resolution and consumer idempotency are accepted. |

## Responsibility boundary

### Comment Service owns

- Comment/reply content, author UUID, post UUID, parent relation and timestamps.
- One-level reply invariant, owner-only edit/delete and soft-delete placeholder behavior.
- Exact active comment count per post.
- Internal Comment availability classification for Interaction.

It does not own Post visibility, user/profile data, reactions, bookmarks or notifications.

### Interaction Service owns

- The reaction ledger `(actor, target type, target id, reaction type)`.
- LIKE/CLAP coexistence, duplicate idempotency and idempotent removal.
- Exact LIKE/CLAP counters and actor reaction state.

It does not own target content/visibility, profiles, bookmarks or notifications.

## Why the current synchronous flow is sound

1. Gateway authentication supplies the actor; command bodies cannot choose another actor.
2. A create command validates its remote target through an application port and fails closed on an unverifiable target.
3. Only local state is mutated in the service transaction. Interaction ledger and counter changes commit or roll back together.
4. Duplicate add and repeated remove are normal idempotent outcomes, not generic database-error masking.
5. Read-only local endpoints do not call Post/Comment, so a provider outage does not take counter or discussion reads down.
6. Post deletion cleanup is asynchronous: each service applies an idempotent local transaction;
   Comment records the downstream cleanup event in its outbox before commit.

## Deliberate limitations and learning debt

- `Comment` and Interaction persistence models still carry JPA annotations in the domain package. This is a pragmatic Tactical DDD compromise; separating pure aggregates from persistence entities is an optional refactor, not a release blocker.
- Availability calls execute inside application methods marked transactional. PostgreSQL connections are normally acquired lazily, but a later hardening pass may separate remote validation from the shortest possible database transaction.
- Entity timestamps use `LocalDateTime`. Future integration-event timestamps must use UTC `Instant`/offset values.
- A shared static internal token is appropriate for this project environment. Production would require secret management, rotation and preferably workload identity/mTLS.
- Counter repair is a runbook operation; no public repair endpoint is intentionally exposed.
- Module tests cover listener configuration and outbox relay behavior, but the current E2E does not
  start a real Kafka broker or prove Post producer reliability, DLT replay or Notification behavior.

## Learning coverage

The implementation demonstrates bounded contexts and ADRs, Tactical DDD ports/use cases, database-per-service, Flyway, REST contracts, Gateway trust boundaries, Eureka discovery, OpenFeign, failure classification, timeout/retry/circuit breaker, idempotency, database concurrency, batch composition, correlation, metrics, Testcontainers, WireMock and real-service E2E.

The accepted deletion slice now demonstrates transaction plus outbox, versioned Kafka event,
idempotent cleanup, bounded retry and DLT. Future asynchronous work should only be added for an
accepted business contract and must include replay and end-to-end verification.

## Release gate

Comment and Interaction may be treated as complete for the current owned scope when their module
tests and Docker-backed PostgreSQL integration tests remain green. The whole social-media flow is
complete only after the cross-team handoff checklist in
`docs/handoffs/comment-interaction-team-handoff.md` is closed.
