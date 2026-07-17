# Comment/Interaction implementation status

## Completed

- Phase 0: ADR-001..007, glossary, baseline, API contracts and changelogs.
- Phase 1: trusted actor header, UUID parsing at HTTP boundary, correlation response and stable local error mapping.
- Phase 2: create/reply/edit/idempotent delete/get/paginated Comment use cases, one-level reply invariants and PostgreSQL repository integration coverage for Flyway, deleted-parent visibility and pagination.
- Phase 3: add/remove/current reaction, exact counters, batch counter endpoint and PostgreSQL integration coverage for concurrent idempotency, rollback, safe removal and legacy BOOKMARK cleanup.
- Phase 4: application availability ports, Eureka Feign adapters, explicit path-variable contracts, correlation/internal credential propagation, Comment internal availability endpoint and WireMock consumer-contract coverage.
- Phase 5: explicit connect/read timeouts, bounded retry, non-retriable 4xx classification, per-dependency circuit breakers, fail-closed 503 semantics and tested closed/open/half-open recovery.
- Phase 7: batch Interaction counter API plus single/batch active Comment counts, avoiding per-post Comment queries for Feed/BFF composition.
- Phase 8 (local-command slice): correlation-aware structured command logs, bounded-cardinality business metrics, Actuator metrics/circuit endpoints and an operations runbook.
- Phase 9 (synchronous slice): real Comment and Interaction applications, isolated PostgreSQL databases and HTTP/Feign E2E coverage for duplicate reaction, idempotent removal, deleted targets and dependency failure.

## Intentionally gated

- Phase 6 event/outbox work is gated by acceptance of CR-NOTIFICATION-001. Publishing events with no compatible UUID consumer would violate the roadmap's event ownership rule.
- The preferred Post internal availability endpoint remains proposed in CR-POST-001. The current adapter consumes the existing `GET /api/v1/posts/{id}` contract and classifies only `PUBLIC` as available.
- Kafka/DLT/outbox and Post/Notification-dependent E2E remain gated by the provider contracts described above; the owned Comment-to-Interaction synchronous flow is covered locally.
- Kafka publish/consume/DLT and outbox pending/age metrics remain gated with Phase 6; local Comment/Interaction metrics do not depend on that contract.
- PostgreSQL integration tests require a running Docker engine and are part of the standard Comment/Interaction Maven test suite.
