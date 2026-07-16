# Comment/Interaction implementation status

## Completed

- Phase 0: ADR-001..007, glossary, baseline, API contracts and changelogs.
- Phase 1: trusted actor header, UUID parsing at HTTP boundary, correlation response and stable local error mapping.
- Phase 2: create/reply/edit/idempotent delete/get/paginated Comment use cases and one-level reply invariants.
- Phase 3: add/remove/current reaction, exact counters, batch counter endpoint, BOOKMARK cleanup and concurrent idempotency test.
- Phase 4: application availability ports, Eureka Feign adapters, correlation/internal credential propagation and Comment internal availability endpoint.
- Phase 5: explicit connect/read timeouts, bounded retry, per-dependency circuit breakers and fail-closed 503 semantics.
- Phase 7 (batch-first slice): batch Interaction counter API.
- Phase 8 (baseline): correlation logs, Actuator metrics/circuit endpoints and an operations runbook.

## Intentionally gated

- Phase 6 event/outbox work is gated by acceptance of CR-NOTIFICATION-001. Publishing events with no compatible UUID consumer would violate the roadmap's event ownership rule.
- The preferred Post internal availability endpoint remains proposed in CR-POST-001. The current adapter consumes the existing `GET /api/v1/posts/{id}` contract and classifies only `PUBLIC` as available.
- PostgreSQL Testcontainers, Kafka/DLT/outbox integration tests and cross-service E2E require the provider contracts and Docker environment; unit/web/concurrency tests are included now.
