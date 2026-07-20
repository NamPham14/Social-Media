# Comment/Interaction implementation status

## Completed

- Phase 0: ADR-001..007, glossary, baseline, API contracts and changelogs.
- Phase 1: trusted actor header, UUID parsing at HTTP boundary, correlation response and stable local error mapping.
- Phase 2: create/reply/edit/idempotent delete/get/paginated Comment use cases, one-level reply invariants and PostgreSQL repository integration coverage for Flyway, deleted-parent visibility and pagination.
- Phase 3: add/remove/current reaction, exact counters, batch counter endpoint and PostgreSQL integration coverage for concurrent idempotency, rollback, safe removal and legacy BOOKMARK cleanup.
- Phase 4: application availability ports, Eureka Feign adapters, explicit path-variable contracts, correlation/internal credential propagation, Comment internal availability endpoint and WireMock consumer-contract coverage.
- Phase 5: explicit connect/read timeouts, bounded retry, non-retriable 4xx classification, per-dependency circuit breakers, fail-closed 503 semantics and tested closed/open/half-open recovery.
- Phase 6a (deletion cleanup): idempotent `PostDeleted` consumers, transactional Comment outbox,
  polling relay for `PostCommentsDeletedV1`, bounded listener retry, service-specific DLTs and
  acknowledged DLT publishing.
- Phase 6b (notification producers): transactional Comment/Interaction outboxes for
  `CommentCreatedV1`, `CommentRepliedV1` and `ReactionCreatedV1`, resolved UUID recipients,
  self/duplicate suppression, acknowledged relay publishing and rollback/commit coverage.
- Phase 7: batch Interaction counter API plus single/batch active Comment counts, avoiding per-post Comment queries for Feed/BFF composition.
- Phase 8 (local-command slice): correlation-aware structured command logs, bounded-cardinality business metrics, Actuator metrics/circuit endpoints and an operations runbook.
- Phase 9 (synchronous slice): real Comment and Interaction applications, isolated PostgreSQL databases and HTTP/Feign E2E coverage for duplicate reaction, idempotent removal, deleted targets and dependency failure.

## Intentionally gated

- Notification UUID consumers remain cross-team work under CR-NOTIFICATION-001. Producer contracts,
  topics and outboxes are complete; the current Long-based Notification model must not consume them yet.
- The preferred Post internal availability endpoint remains proposed in CR-POST-001. The current adapter consumes the existing `GET /api/v1/posts/{id}` contract and classifies only `PUBLIC` as available.
- Kafka/Post-dependent full-flow E2E remains cross-team work; local listener, retry/DLT configuration
  and outbox relay behavior are covered by module tests.
- Outbox pending-count/oldest-age alerts and managed Kafka topic provisioning remain deployment work.
- PostgreSQL integration tests require a running Docker engine and are part of the standard Comment/Interaction Maven test suite.

## Final audit and team handoff

- The final scope verdict and deliberate limitations are recorded in `comment-interaction-final-audit.md`.
- Cross-team provider tasks and rollout order are recorded in `../handoffs/comment-interaction-team-handoff.md`.
- PlantText-ready system, Comment, Interaction and proposed event flows are stored under `../diagrams/` as `.puml` files.
