# Comment/Interaction implementation status

## Completed

- Phase 0: ADR-001..007, glossary, baseline, API contracts and changelogs.
- Phase 1: trusted actor header, UUID parsing at HTTP boundary, correlation response and stable local error mapping.
- Phase 2: create/reply/edit/idempotent delete/get/paginated Comment use cases, one-level reply invariants and PostgreSQL repository integration coverage for Flyway, deleted-parent visibility and pagination.
- Phase 3: add/remove/current reaction, exact counters, batch counter endpoint and PostgreSQL integration coverage for concurrent idempotency, rollback, safe removal and legacy BOOKMARK cleanup.
- Phase 4: application availability ports, Eureka Feign adapters, explicit path-variable contracts, correlation/internal credential propagation, Comment internal availability endpoint and WireMock consumer-contract coverage.
- Phase 5: explicit connect/read timeouts, bounded retry, non-retriable 4xx classification, per-dependency circuit breakers, fail-closed 503 semantics and tested closed/open/half-open recovery.
- Phase 7 (batch-first slice): batch Interaction counter API.
- Phase 8 (baseline): correlation logs, Actuator metrics/circuit endpoints and an operations runbook.

## Intentionally gated

- Phase 6 event/outbox work is gated by acceptance of CR-NOTIFICATION-001. Publishing events with no compatible UUID consumer would violate the roadmap's event ownership rule.
- The preferred Post internal availability endpoint remains proposed in CR-POST-001. The current adapter consumes the existing `GET /api/v1/posts/{id}` contract and classifies only `PUBLIC` as available.
- Kafka/DLT/outbox integration tests and cross-service E2E remain gated by the provider contracts described above.
- PostgreSQL integration tests require a running Docker engine and are part of the standard Comment/Interaction Maven test suite.
